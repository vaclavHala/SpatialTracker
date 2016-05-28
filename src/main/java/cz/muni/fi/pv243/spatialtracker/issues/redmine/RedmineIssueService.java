package cz.muni.fi.pv243.spatialtracker.issues.redmine;

import cz.muni.fi.pv243.spatialtracker.Closeable;
import static cz.muni.fi.pv243.spatialtracker.Closeable.closeable;
import cz.muni.fi.pv243.spatialtracker.CustomField;
import cz.muni.fi.pv243.spatialtracker.MulticauseError;
import cz.muni.fi.pv243.spatialtracker.ServerError;
import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.INTEGRATION_PROJECT_ID;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.redmine.dto.RedmineIssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.IssueService;
import cz.muni.fi.pv243.spatialtracker.issues.IssueStatus;
import cz.muni.fi.pv243.spatialtracker.issues.dto.Coordinates;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsFull;
import cz.muni.fi.pv243.spatialtracker.issues.filter.IssueFilter;
import cz.muni.fi.pv243.spatialtracker.issues.redmine.dto.*;
import cz.muni.fi.pv243.spatialtracker.issues.redmine.filter.RedmineFilterComposer;
import static cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.assembleBasicAuthHeader;
import cz.muni.fi.pv243.spatialtracker.users.redmine.RedmineUserService;
import cz.muni.fi.pv243.spatialtracker.users.redmine.dto.RedmineUserDetails;
import static java.lang.String.format;
import java.util.ArrayList;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
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
    @Property(INTEGRATION_PROJECT_ID)
    private int projectId;

    @Inject
    private Client restClient;

    @Inject
    private RedmineUserService userService;

    @Inject
    private RedmineFilterComposer filterComposer;

    @Inject
    private RedmineCategoryMapper categoryMapper;

    @Inject
    private RedminePriorityMapper priorityMapper;

    @Inject
    private RedmineStatusMapper statusMapper;

    @Inject
    private RedmineCoordinatesMapper coordsMapper;

    @Override
    public long report(final IssueCreate newIssue, final String login) throws MulticauseError {
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
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header("X-Redmine-Switch-User", login)
                                .header("X-Redmine-API-Key", this.redmineKey)
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
    public void updateIssueState(final long issueId, final IssueStatus newStatus) {
        log.info("Reguest to update status of issue #{} to {}", issueId, newStatus);
        RedmineIssueUpdateStatus redmineUpdate =
                new RedmineIssueUpdateStatus(this.statusMapper.toId(newStatus));
        WebTarget target = this.restClient.target(this.redmineUrl).path("issues").path(issueId + ".json");
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header("X-Redmine-API-Key", this.redmineKey)
                                .buildPut(json(redmineUpdate))
                                .invoke())) {
            if (redmineResponse.get().getStatus() == 200) {
                log.info("Update succeeded");
            } else if (redmineResponse.get().getStatus() == 401) {
                throw new NotAuthorizedException(Response.status(401).build());
            } else if (redmineResponse.get().getStatus() == 404) {
                throw new NotFoundException();
            } else {
                throw new ServerError("boom");
            }
        } catch (ProcessingException e) {
            throw new ServerError(e);
        }
        //Redmine responds with OK even if nothing changed, we need to check manually
        IssueDetailsFull updatedDetails = this.detailsFor(issueId).get();
        if (updatedDetails.status().equals(newStatus)) {
            log.info("Status of issue #{} was updated to {}", issueId, newStatus);
            return;
        } else {
            log.info("Illegal state transition of issue #{}, wanted {} -> {}",
                     issueId, updatedDetails.status(), newStatus);
            throw new ForbiddenException();
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

                RedmineIssueDetails redmineIssueDetails =
                        redmineResponse.get().readEntity(RedmineIssueDetailsSingleWrapper.class).issue();
                RedmineUserDetails redmineUserdetails =
                        this.userService.detailsRedmineSomeUser(redmineIssueDetails.author().id());
                log.debug("Issue #{}: {}", issueId, redmineIssueDetails);
                return Optional.of(new IssueDetailsFull(redmineIssueDetails.subject(),
                                                        redmineIssueDetails.description(),
                                                        this.statusMapper.fromId(redmineIssueDetails.status().id()),
                                                        this.priorityMapper.fromId(redmineIssueDetails.priority().id()),
                                                        this.categoryMapper.fromId(redmineIssueDetails.category().id()),
                                                        redmineIssueDetails.startedDate(),
                                                        redmineUserdetails.login(),
                                                        this.coordsMapper.readFrom(redmineIssueDetails.customFields())));
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

    @Override
    public List<IssueDetailsBrief> searchFiltered(final List<IssueFilter> filters) {
        String redmineQueryFilter = this.filterComposer.assembleFilters(filters);
        log.debug("Composed redmine filter: {}", redmineQueryFilter);

        return this.getAllIssuesFor(redmineQueryFilter).stream()
                   .map((RedmineIssueDetails redmineIssue) -> {
                       Coordinates coords = this.coordsMapper.readFrom(redmineIssue.customFields());
                       return new IssueDetailsBrief(redmineIssue.id(),
                                                    redmineIssue.subject(),
                                                    coords);
                   }).collect(toList());
    }

    /**
     * Redmine wont let us get page bigger than 100 results.
     * We query consecutive pages (offset 0, offset 100, offset 200 ...)
     * as long as we keep getting full 100 results.
     * Once we get smaller result it means it is the last page and we quit.
     */
    private List<RedmineIssueDetails> getAllIssuesFor(final String filter) {
        List<RedmineIssueDetails> aggregatedRedmineIssues = new ArrayList<>();
        int offset = 0;
        int count = 100;
        while (true) {
            List<RedmineIssueDetails> issuesPage = this.getPage(count, offset, filter);
            aggregatedRedmineIssues.addAll(issuesPage);
            offset += count;
            if (issuesPage.size() != count) {
                break;
            }
        }
        return aggregatedRedmineIssues;
    }

    private List<RedmineIssueDetails> getPage(final int count, final int offset, final String filter) {
        String issueUrl = format("%s/issues.json?project_id=%d&limit=%d&offset=%d&%s",
                                 this.redmineUrl,
                                 this.projectId,
                                 count, offset, filter);
        WebTarget target = this.restClient.target(issueUrl);
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .buildGet()
                                .invoke())) {
            if (redmineResponse.get().getStatus() == 200) {
                List<RedmineIssueDetails> redmineIssues =
                        redmineResponse.get().readEntity(RedmineIssueDetailsListWrapper.class).issues();
                log.debug("Got {} issues for limit={}, offset={}",
                          redmineIssues.size(), count, offset);
                return redmineIssues;
            } else {
                log.warn("Could not fetch issues: " + redmineResponse.get().readEntity(String.class));
                //                List<String> errors = this.extractErrorReport(redmineResponse.get());
                List<String> errors = emptyList();
                return emptyList();
            }
        } catch (ProcessingException e) {
            throw new ServerError(e);
        }
    }

}
