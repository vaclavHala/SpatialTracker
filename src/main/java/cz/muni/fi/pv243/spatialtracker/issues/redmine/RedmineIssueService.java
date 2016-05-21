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
import cz.muni.fi.pv243.spatialtracker.issues.redmine.dto.RedmineIssueCreateWrapper;
import static cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.assembleBasicAuthHeader;
import static cz.muni.fi.pv243.spatialtracker.users.UserGroup.LOGGED_IN;
import cz.muni.fi.pv243.spatialtracker.users.redmine.dto.RedmineUserCreateResponse;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import java.util.List;
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
    private RedmineCategoryMapper categoryMapper;

    @Inject
    private RedminePriorityMapper priorityMapper;

    @Inject
    private RedmineStatusMapper statusMapper;

    @Override
    public long report(final IssueCreate newIssue, final String login, final String password) throws MulticauseError {
        log.info("New issue creation reguest: {}", newIssue);
        RedmineIssueCreate redmineNewIssue =
                new RedmineIssueCreate(SPATIAL_PROJECT_ID,
                                       SPATIAL_TRACKER_ID,
                                       newIssue.subject(),
                                       this.priorityMapper.toId(newIssue.priority()),
                                       asList(new CustomField(2, null, newIssue.coords().longitude()),
                                              new CustomField(3, null, newIssue.coords().latitude())));
        WebTarget target = this.restClient.target(this.redmineUrl + "issues.json");
        String auth = assembleBasicAuthHeader(login, password);
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header(AUTHORIZATION, auth)
                                .buildPost(json(new RedmineIssueCreateWrapper(redmineNewIssue)))
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
    public IssueDetailsFull detailsOf(long issueId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
