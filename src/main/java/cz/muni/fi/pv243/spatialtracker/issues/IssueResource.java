package cz.muni.fi.pv243.spatialtracker.issues;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.pv243.spatialtracker.MulticauseError;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsFull;
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
@RolesAllowed("USER")
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
    public Response createIssue(
            final @NotNull @Valid IssueCreate issue,
            final @Context UriInfo userApiLocation) throws MulticauseError {
        String user = this.ctx.getCallerPrincipal().getName();
        log.info("New issue report from user {}: {}", user, issue);
        long newIssueResourceId = this.issueService.report(issue, user);
        URI newIssueResourcePath = userApiLocation.getAbsolutePathBuilder()
                                                  .path(String.valueOf(newIssueResourceId)).build();
        return Response.created(newIssueResourcePath).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response detailsFor(final @PathParam("id") long forId) {
        log.info("Request to display isue #{}", forId);
        Optional<IssueDetailsFull> issue = this.issueService.detailsFor(forId);
        if (issue.isPresent()) {
            log.info("Issue #{} was found", forId);
            log.debug("Issue #{}: {}", forId, issue.get());
            return Response.ok(issue.get()).build();
        } else {
            log.info("Issue #{} does not exist", forId);
            return Response.status(404).build();
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
        List<IssueFilter> filters = getFilters(rawFilter);
        log.debug("Searching for issues using filters: {}", filters);
        List<IssueDetailsBrief> foundIssues = this.issueService.searchFiltered(filters);
        log.debug("Found {} issues: {}", foundIssues.size(), foundIssues);
        return Response.ok(foundIssues).build();
    }

    public List<IssueFilter> getFilters(String rawFilter) throws MulticauseError {
        List<IssueFilter> filters;
        try {
            filters = this.json.readValue(rawFilter, ISSUE_FILTERS_LIST_TOKEN);
        } catch (IOException ex) {
            log.warn("{Invalid filter:}", ex);
            throw new MulticauseError(asList("Invalid issue filter"));
        }
        return filters;
    }
}
