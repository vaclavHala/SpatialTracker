package cz.muni.fi.pv243.spatialtracker.issues;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.pv243.spatialtracker.ErrorReport;
import cz.muni.fi.pv243.spatialtracker.MulticauseError;
import cz.muni.fi.pv243.spatialtracker.common.BackendServiceException;
import cz.muni.fi.pv243.spatialtracker.common.IllegalOperationException;
import cz.muni.fi.pv243.spatialtracker.common.InvalidInputException;
import cz.muni.fi.pv243.spatialtracker.common.UnauthorizedException;
import cz.muni.fi.pv243.spatialtracker.common.NotFoundException;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsFull;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueUpdateStatus;
import cz.muni.fi.pv243.spatialtracker.issues.filter.IssueFilter;
import java.io.IOException;
import java.net.URI;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/issue")
@DeclareRoles("USER")
public class IssueResource {

    private static final TypeReference<List<IssueFilter>> ISSUE_FILTERS_LIST_TOKEN =
            new TypeReference<List<IssueFilter>>() {};

    @Resource
    private EJBContext ctx;

    @Inject
    private IssueService issueService;

    @Inject
    private ObjectMapper json;

    @POST
    @RolesAllowed({"USER", "WORKER"})
    public Response createIssue(
            final @NotNull @Valid IssueCreate issue,
            final @Context UriInfo userApiLocation) throws MulticauseError {
        String user = this.ctx.getCallerPrincipal().getName();
        log.info("New issue report from user {}: {}", user, issue);
        try {
            long newIssueResourceId = this.issueService.report(issue, user);
            URI newIssueResourcePath = userApiLocation.getAbsolutePathBuilder()
                                                      .path(String.valueOf(newIssueResourceId)).build();
            log.info("Issue was created at {}", newIssueResourcePath);
            return Response.created(newIssueResourcePath).build();
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized issue creation attempt", e);
            return Response.status(401).build();
        } catch (InvalidInputException e) {
            log.warn("Error in issue creation attempt", e);
            Response.ResponseBuilder builder = Response.status(400);
            if (!e.errors().isEmpty()) {
                ErrorReport report = new ErrorReport(e.errors());
                builder.entity(report);
            }
            return builder.build();
        } catch (BackendServiceException e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/{id}")
    @RolesAllowed("WORKER")
    public Response updateIssueState(
            final @NotNull @Valid IssueUpdateStatus statusUpdate,
            final @PathParam("id") long forId) {
        log.info("Request to update status of isue #{} to {}",
                 forId, statusUpdate.status());
        try {
            this.issueService.updateIssueState(forId, statusUpdate.status());
            log.info("Issue status was updated in Redmine");
            return Response.status(204).build();
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized issue state update", e);
            return Response.status(401).build();
        } catch (IllegalOperationException e) {
            log.warn("Illegal issue state transition", e);
            return Response.status(403).build();
        } catch (NotFoundException e) {
            log.warn("Request to update state of non-existent issue", e);
            return Response.status(404).build();
        } catch (BackendServiceException e) {
            log.warn("Internal error when updating issue status", e);
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response detailsFor(final @PathParam("id") long forId) {
        log.info("Request to display isue #{}", forId);
        try {
            IssueDetailsFull issue = this.issueService.detailsFor(forId);
            log.info("Issue #{} was found", forId);
            log.debug("Issue #{}: {}", forId, issue);
            return Response.ok(issue).build();
        } catch (NotFoundException e) {
            log.warn("Request to view details of non-existent issue", e);
            return Response.status(404).build();
        } catch (BackendServiceException e) {
            log.warn("Internal error when updating issue status", e);
            return Response.status(500).build();
        }
    }

    /**
     * Example filter: {@code [{"@type":"category","in":["ADD","REMOVE"]},
     *                         {"@type":"spatial","lat_min":1.0,"lat_max":10.0,"lon_min":2.0,"lon_max":12.0}]}
     * @param rawFilter contains JSON string describing the query.
     *                  Its structure is {@code filter=[{filter_1},{filter_2},...]},
     *                  where {@code filter_n} is a JSON object describing single specific search criterion.
     *                  In particular it must be serializable to some subclass of {@link IssueFilter}.
     *                  Note the rawFilter needs to be URL encoded, i.e. {@code issue/?filter=%5B%7B%22%40type...}.
     *                  The '?' and '=' must not be encoded, only the JSON body.
     */
    @GET
    @PermitAll
    public Response searchFiltered(final @QueryParam("filter") String rawFilter) throws MulticauseError {
        List<IssueFilter> filters;
        try {
            filters = this.json.readValue(rawFilter, ISSUE_FILTERS_LIST_TOKEN);
        } catch (IOException e) {
            log.warn("Trying to search for issues using invalid filter: {}", rawFilter, e);
            return Response.status(400)
                           .entity(new ErrorReport(asList("Invalid issue filter")))
                           .build();
        }
        log.debug("Searching for issues using filters: {}", filters);
        try {
            List<IssueDetailsBrief> foundIssues = this.issueService.searchFiltered(filters);
            log.debug("Found {} issues: {}", foundIssues.size(), foundIssues);
            return Response.ok(foundIssues).build();
        } catch (InvalidInputException e) {
            log.warn("Trying to search for issues using invalid filter set", e);
            return Response.status(400).build();
        } catch (BackendServiceException e) {
            log.warn("Internal error when searching for issues", e);
            return Response.status(500).build();
        }
    }
}
