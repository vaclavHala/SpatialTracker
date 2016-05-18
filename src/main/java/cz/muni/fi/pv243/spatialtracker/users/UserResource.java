package cz.muni.fi.pv243.spatialtracker.users;

import cz.muni.fi.pv243.spatialtracker.MulticauseError;
import cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.LoginPass;
import static cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.decodeBasicAuthLogin;
import cz.muni.fi.pv243.spatialtracker.users.dto.*;
import cz.muni.fi.pv243.spatialtracker.users.redmine.RedmineUserService;
import java.net.URI;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
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
@Path("/user")
public class UserResource {

    @Inject
    private RedmineUserService usersRedmine;

    @POST
    public Response register(
            final @Valid @NotNull UserCreate newUser,
            final @Context UriInfo userApiLocation) throws MulticauseError {
        log.info("New user registration reguest: {}", newUser);

        String newUserResourceName = this.usersRedmine.register(newUser);
        URI newUserResourcePath = userApiLocation.getAbsolutePathBuilder()
                                                 .path(newUserResourceName).build();
        return Response.created(newUserResourcePath).build();
    }

    @GET
    @Path("/{login}")
    public Response detailsFor(final @PathParam("login") String forLogin) {
        log.info("Request to display user: {}", forLogin);
        Optional<UserDetails> user = this.usersRedmine.detailsSomeUser(forLogin);
        if (user.isPresent()) {
            log.info("User <{}> was found", forLogin);
            log.debug("User <{}>: {}", forLogin, user.get());
            return Response.ok(user.get()).build();
        } else {
            log.info("User <{}> does not exist", forLogin);
            return Response.status(404).build();
        }
    }

    @GET
    @Path("/me")
    public Response detailsMe(final @HeaderParam(AUTHORIZATION) String currentUserBasicAuth) throws MulticauseError {
        LoginPass auth = decodeBasicAuthLogin(currentUserBasicAuth);
        if (auth == null) {
            log.info("Got details request for current user with invalid Auth header value: {}",
                     currentUserBasicAuth);
            return Response.status(403).build();
        }
        return Response.ok(this.usersRedmine.detailsCurrentUser(auth.login(), auth.pass())).build();
    }

    @PUT
    @Path("/me")
    public Response updateMe(UserCreate newUser) {
        System.out.println("update me");
        return null;
    }

    @DELETE
    @Path("/me")
    public Response deleteMe(final @HeaderParam(AUTHORIZATION) String currentUserBasicAuth) throws MulticauseError {
        LoginPass auth = decodeBasicAuthLogin(currentUserBasicAuth);
        if (auth == null) {
            log.info("Got delete request for current user with invalid Auth header value: {}",
                     currentUserBasicAuth);
            return Response.status(403).build();
        }
        this.usersRedmine.deleteCurrentUser(auth.login(), auth.pass());
        return Response.noContent().build();

    }
}
