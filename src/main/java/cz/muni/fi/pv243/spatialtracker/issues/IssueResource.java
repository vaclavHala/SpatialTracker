
package cz.muni.fi.pv243.spatialtracker.issues;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import cz.muni.fi.pv243.spatialtracker.issues.jms.IssueStatusUpdatedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/issue")
@DeclareRoles("USER")
public class IssueResource {

    private static final TypeReference<List<IssueFilter>> ISSUE_FILTERS_LIST_TOKEN =
            new TypeReference<List<IssueFilter>>() {
            };

    @Resource
    private EJBContext ctx;

    @Inject
    private IssueService issueService;

    @Inject
    private ObjectMapper json;

    @Inject
    private Event<IssueStatusUpdatedEvent> issueUpdatedEvent;

    @POST
    @RolesAllowed({"USER", "WORKER"})
    public Response createIssue(
            final @NotNull @Valid IssueCreate issue,
            final @Context UriInfo userApiLocation) throws UnauthorizedException, InvalidInputException, BackendServiceException {
        String user = this.ctx.getCallerPrincipal().getName();
        log.info("New issue report from user {}: {}", user, issue);
        long newIssueResourceId = this.issueService.report(issue, user);
        URI newIssueResourcePath = userApiLocation.getAbsolutePathBuilder()
                .path(String.valueOf(newIssueResourceId)).build();
        log.info("Issue was created at {}", newIssueResourcePath);
        return Response.created(newIssueResourcePath).build();
    }

    @POST
    @Path("/{id}")
    @RolesAllowed("WORKER")
    public Response updateIssueState(
            final @NotNull @Valid IssueUpdateStatus statusUpdate,
            final @PathParam("id") long forId) throws UnauthorizedException, NotFoundException, IllegalOperationException, BackendServiceException {
        log.info("Request to update status of isue #{} to {}",


                forId, statusUpdate.status());
        this.issueService.updateIssueState(forId, statusUpdate.status());
        log.info("Issue status was updated in Redmine");
        issueUpdatedEvent.fire(new IssueStatusUpdatedEvent(forId, statusUpdate.status()));
        log.info("Fired issue updated event.");
        return Response.status(204).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response detailsFor(
            final @PathParam("id") long forId) throws NotFoundException, BackendServiceException {
        log.info("Request to display isue #{}", forId);
        IssueDetailsFull issue = this.issueService.detailsFor(forId);
        log.info("Issue #{} was found", forId);
        log.debug("Issue #{}: {}", forId, issue);
        return Response.ok(issue).build();
    }

    /**
     * Example filter: {@code [{"@type":"category","in":["ADD","REMOVE"]},
     * {"@type":"spatial","lat_min":1.0,"lat_max":10.0,"lon_min":2.0,"lon_max":12.0}]}
     *
     * @param rawFilter contains JSON string describing the query.
     *                  Its structure is {@code filter=[{filter_1},{filter_2},...]},
     *                  where {@code filter_n} is a JSON object describing single specific search criterion.
     *                  In particular it must be serializable to some subclass of {@link IssueFilter}.
     *                  Note the rawFilter needs to be URL encoded, i.e. {@code issue/?filter=%5B%7B%22%40type...}.
     *                  The '?' and '=' must not be encoded, only the JSON body.
     */
    @GET
    @PermitAll
    public Response searchFiltered(
            final @QueryParam("filter") String rawFilter) throws InvalidInputException, BackendServiceException {
        List<IssueFilter> filters;
        try {
            filters = getFilters(rawFilter);
        } catch (IOException e) {
            log.warn("Trying to search for issues using invalid filter: {}", rawFilter, e);
            throw new InvalidInputException(asList("Malformed issue filter"));
        }
        log.debug("Searching for issues using filters: {}", filters);
        List<IssueDetailsBrief> foundIssues = this.issueService.searchFiltered(filters);
        log.debug("Found {} issues: {}", foundIssues.size(), foundIssues);
        return Response.ok(foundIssues).build();
    }

    public List<IssueFilter> getFilters(String rawFilter) throws IOException {
        return this.json.readValue(rawFilter, ISSUE_FILTERS_LIST_TOKEN);
    }
}