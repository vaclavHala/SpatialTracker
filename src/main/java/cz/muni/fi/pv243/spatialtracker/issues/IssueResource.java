package cz.muni.fi.pv243.spatialtracker.issues;

import cz.muni.fi.pv243.spatialtracker.AuthenticationException;
import cz.muni.fi.pv243.spatialtracker.MulticauseError;
import cz.muni.fi.pv243.spatialtracker.issues.dto.Coordinates;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsFull;
import cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils;
import cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.LoginPass;
import static cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.decodeBasicAuthLogin;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserCreate;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserDetails;
import java.net.URI;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/issue")
public class IssueResource {

    @Inject
    private IssueService issueService;

    @POST
    public Response createIssue(
            final @NotNull @Valid IssueCreate issue,
            final @Context UriInfo userApiLocation,
            final @HeaderParam(AUTHORIZATION) String currentUserBasicAuth) throws MulticauseError {

        LoginPass auth = decodeBasicAuthLogin(currentUserBasicAuth);
        if (auth == null) {
            log.info("Got details request for current user with invalid Auth header value: {}",
                     currentUserBasicAuth);
            throw new AuthenticationException();
        }

        log.info("New issue report: {}", issue);
        long newIssueResourceId = this.issueService.report(issue, auth.login(), auth.pass());
        URI newIssueResourcePath = userApiLocation.getAbsolutePathBuilder()
                                                  .path(String.valueOf(newIssueResourceId)).build();
        return Response.created(newIssueResourcePath).build();
    }

    @GET
    @Path("/{login}")
    public Response detailsFor(final @PathParam("login") long forId) {
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

    //    @GET
    //    public void listIssues() {
    //        String issues = this.restClient.target(this.redmineUrl + "projects.json")
    //                                       .request(APPLICATION_JSON)
    //                                       .header("X-Redmine-API-Key", this.redmineKey)
    //                                       .buildGet()
    //                                       .invoke(String.class);
    //        log.info("issues {}", issues);
    //    }
}
