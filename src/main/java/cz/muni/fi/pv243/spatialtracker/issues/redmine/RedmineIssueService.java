package cz.muni.fi.pv243.spatialtracker.issues.redmine;

import cz.muni.fi.pv243.spatialtracker.Closeable;
import static cz.muni.fi.pv243.spatialtracker.Closeable.closeable;
import cz.muni.fi.pv243.spatialtracker.CustomField;
import cz.muni.fi.pv243.spatialtracker.MulticauseError;
import cz.muni.fi.pv243.spatialtracker.ServerError;
import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.redmine.dto.RedmineIssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.IssueService;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsFull;
import cz.muni.fi.pv243.spatialtracker.issues.redmine.dto.RedmineIssueCreateResponse;
import cz.muni.fi.pv243.spatialtracker.issues.redmine.dto.RedmineIssueDetails;
import static cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.assembleBasicAuthHeader;
import cz.muni.fi.pv243.spatialtracker.users.redmine.RedmineUserService;
import static java.lang.String.format;
import java.util.ArrayList;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import static javax.ws.rs.client.Entity.json;
import javax.ws.rs.client.WebTarget;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@Path("/issue")
public class RedmineIssueService implements IssueService {

    private static long SPATIAL_PROJECT_ID = 4;
    private static long SPATIAL_TRACKER_ID = 1;

    @Inject
    @Property(REDMINE_API_KEY)
    private String redmineKey;
    @Inject
    @Property(REDMINE_BASE_URL)
    private String redmineUrl;

    @Inject
    private Client restClient;

    @Inject
    private RedmineUserService userService;

    @Inject
    private RedmineCategoryMapper categoryMapper;

    @Inject
    private RedminePriorityMapper priorityMapper;

    @Inject
    private RedmineStatusMapper statusMapper;

    @Inject
    private RedmineCoordinatesMapper coordsMapper;

    @Override
    public long report(final IssueCreate newIssue, final String login, final String password) throws MulticauseError {
        log.info("New issue creation reguest: {}", newIssue);
        List<CustomField> customFields = new ArrayList<>();
        this.coordsMapper.appendTo(customFields, newIssue.coords());
        RedmineIssueCreate redmineNewIssue =
                new RedmineIssueCreate(SPATIAL_PROJECT_ID,
                                       SPATIAL_TRACKER_ID,
                                       newIssue.subject(),
                                       newIssue.description(),
                                       this.categoryMapper.toId(newIssue.category()),
                                       this.priorityMapper.toId(newIssue.priority()),
                                       customFields);
        WebTarget target = this.restClient.target(this.redmineUrl + "issues.json");
        String auth = assembleBasicAuthHeader(login, password);
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header(AUTHORIZATION, auth)
                                .buildPost(json(redmineNewIssue))
                                .invoke())) {
            if (redmineResponse.get().getStatus() == 201) {
                log.info("Issue was created in Redmine");
                RedmineIssueCreateResponse redmineResp =
                        redmineResponse.get().readEntity(RedmineIssueCreateResponse.class);
                return redmineResp.issueId();
            } else {
                //                List<String> errors = this.extractErrorReport(redmineResponse.get());
                List<String> errors = emptyList();
                log.info("Failed to create issue: {}", errors);
                throw new MulticauseError(errors);
            }
        } catch (ProcessingException e) {
            throw new ServerError(e);
        }
    }

    @Override
    public Optional<IssueDetailsFull> detailsFor(long issueId) {
        log.info("Full details for issue #{}", issueId);
        String issueUrl = format("%s/issues/%d.json", this.redmineUrl, issueId);
        WebTarget target = this.restClient.target(issueUrl);
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .buildGet()
                                .invoke())) {
            if (redmineResponse.get().getStatus() == 200) {
                log.info("Issue #{} was found in Redmine", issueId);

                RedmineIssueDetails redmineDetails = redmineResponse.get().readEntity(RedmineIssueDetails.class);
                log.debug("Issue #{}: {}", issueId, redmineDetails);
                //this.userService.
                //TODO redmine user service needs to expose operations with user by redmine id
                //visible only for RedmineUserService, not generic UserService
                return Optional.of(new IssueDetailsFull(redmineDetails.subject(),
                                                        redmineDetails.description(),
                                                        this.statusMapper.fromId(redmineDetails.status().id()),
                                                        this.priorityMapper.fromId(redmineDetails.priority().id()),
                                                        this.categoryMapper.fromId(redmineDetails.category().id()),
                                                        redmineDetails.startedDate(),
                                                        "fantomas",//redmineDetails.author().
                                                        this.coordsMapper.readFrom(redmineDetails.customFields())));
            } else {
                //                List<String> errors = this.extractErrorReport(redmineResponse.get());
                List<String> errors = emptyList();
                log.info("Failed to find issue #{}: {}", issueId, errors);
                return Optional.empty();
            }
        } catch (ProcessingException e) {
            throw new ServerError(e);
        }
    }
}