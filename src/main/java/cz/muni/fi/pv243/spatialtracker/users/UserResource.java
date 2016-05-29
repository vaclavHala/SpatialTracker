package cz.muni.fi.pv243.spatialtracker.users;

import cz.muni.fi.pv243.spatialtracker.common.BackendServiceException;
import cz.muni.fi.pv243.spatialtracker.common.ErrorReport;
import cz.muni.fi.pv243.spatialtracker.common.InvalidInputException;
import cz.muni.fi.pv243.spatialtracker.common.UnauthorizedException;
import cz.muni.fi.pv243.spatialtracker.common.NotFoundException;
import cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.LoginPass;
import static cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.decodeBasicAuthLogin;
import cz.muni.fi.pv243.spatialtracker.users.dto.*;
import cz.muni.fi.pv243.spatialtracker.users.redmine.RedmineUserService;
import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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
@DeclareRoles("USER")
@RolesAllowed("USER")
public class UserResource {

    @Inject
    private RedmineUserService usersRedmine;

    @POST
    @PermitAll
    public Response register(
            final @Valid @NotNull UserCreate newUser,
            final @Context UriInfo userApiLocation) throws InvalidInputException, BackendServiceException {
        log.info("New user registration reguest: {}", newUser);

        String newUserResourceName = this.usersRedmine.register(newUser);
        URI newUserResourcePath = userApiLocation.getAbsolutePathBuilder()
                                                 .path(newUserResourceName).build();
        return Response.created(newUserResourcePath).build();
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(LoginPass auth) throws UnauthorizedException, BackendServiceException {
        UserDetails user;
        if (auth == null || (user = usersRedmine.detailsCurrentUser(auth.login(), auth.pass())) == null) {
            throw new UnauthorizedException(auth.login());
        }
        return Response.ok(user).build();
    }

    @GET
    @Path("/{login}")
    @PermitAll
    public Response detailsFor(
            final @PathParam("login") String forLogin) throws NotFoundException, BackendServiceException {
        log.info("Request to display user: {}", forLogin);
        UserDetails user = this.usersRedmine.detailsSomeUser(forLogin);
        log.info("User <{}> was found", forLogin);
        log.debug("User <{}>: {}", forLogin, user);
        return Response.ok(user).build();
    }

    @GET
    @Path("/me")
    public Response detailsMe(
            final @HeaderParam(AUTHORIZATION) String currentUserBasicAuth) throws UnauthorizedException, BackendServiceException {
        LoginPass auth = decodeBasicAuthLogin(currentUserBasicAuth);

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
    public Response deleteMe(
            final @HeaderParam(AUTHORIZATION) String currentUserBasicAuth) throws UnauthorizedException, BackendServiceException {
        LoginPass auth = decodeBasicAuthLogin(currentUserBasicAuth);
        if (auth == null) {
            throw new UnauthorizedException("null Auth header");
        }
        this.usersRedmine.deleteCurrentUser(auth.login(), auth.pass());
        return Response.noContent().build();
    }
}
