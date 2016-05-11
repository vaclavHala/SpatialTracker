package cz.muni.fi.pv243.spatialtracker.users;

import cz.muni.fi.pv243.spatialtracker.ErrorReport;
import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import cz.muni.fi.pv243.spatialtracker.users.dto.*;
import static java.lang.String.format;
import java.util.Base64;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import static javax.ws.rs.client.Entity.json;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@Path("/user")
public class UsersService {

    private static final String EMPTY = "-";

    @Inject
    @Property(REDMINE_API_KEY)
    private String redmineKey;
    @Inject
    @Property(REDMINE_BASE_URL)
    private String redmineUrl;

    @Inject
    private Client restClient;

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response register(
            final @Valid @NotNull UserCreate newUser,
            final @Context UriInfo userApiLocation) {
        log.info("New user registration reguest: {}", newUser);
        RedmineUserCreate redmineNewUser = new RedmineUserCreate(newUser.login(),
                                                                 newUser.password(),
                                                                 EMPTY,
                                                                 EMPTY,
                                                                 newUser.email());
        WebTarget target = this.restClient.target(this.redmineUrl + "users.json");
        Response redmineResponse = target.request(APPLICATION_JSON)
                                         .header("X-Redmine-API-Key", this.redmineKey)
                                         .buildPost(json(redmineNewUser))
                                         .invoke();
        if (redmineResponse.getStatus() == 201) {
            log.info("User was created in Redmine");
            redmineResponse.close();
            return Response.created(userApiLocation.getAbsolutePathBuilder()
                                                   .path(newUser.login()).build())
                           .build();
        } else if (redmineResponse.getStatus() == 422) {
            ErrorReport errors = redmineResponse.readEntity(ErrorReport.class);
            log.info("Failed to create user: {}", errors);
            return Response.status(400)
                           .entity(errors)
                           .build();
        } else {
            if (redmineResponse.hasEntity()) {
                String rawRedmineResponse = redmineResponse.readEntity(String.class);
                log.error("New user was not created in Redmine, "
                          + "unknown response with status <{}> was received from Redmine: {}",
                          redmineResponse.getStatus(), rawRedmineResponse);
            }
            //say sorry or something
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/{login}")
    @Produces(APPLICATION_JSON)
    public Response detailsFor(final @PathParam("login") String forLogin) {
        log.info("Request to display user: {}", forLogin);
        WebTarget target = this.restClient.target(this.redmineUrl + "users.json")
                                          .queryParam("name", forLogin)
                                          .queryParam("limit", 1);
        RedmineUserDetailsSearchWrapper redmineResponse =
                target.request(APPLICATION_JSON)
                      .header("X-Redmine-API-Key", this.redmineKey)
                      .buildGet()
                      .invoke(RedmineUserDetailsSearchWrapper.class);
        UserDetails user = this.extractUser(forLogin, redmineResponse.matchedUsers());
        if (user != null) {
            log.info("User <{}> was found", forLogin);
            return Response.ok(user).build();
        } else {
            log.info("User <{}> does not exist", forLogin);
            return Response.status(404).build();
        }
    }

    private UserDetails extractUser(final String login, final List<RedmineUserDetails> foundUsers) {
        for (RedmineUserDetails match : foundUsers) {
            if (match.login().equals(login)) {
                return this.mapRedmineUser(match);
            }
        }
        return null;
    }

    @GET
    @Path("/me")
    @Produces(APPLICATION_JSON)
    public Response detailsMe(final @HeaderParam(AUTHORIZATION) String currentUserBasicAuth) {
        String currentLogin = this.decodeBasicAuthLogin(currentUserBasicAuth);
        if (currentLogin == null) {
            log.info("Got details request for current user with invalid Auth header value: {}",
                     currentUserBasicAuth);
            return Response.status(403).build();
        }

        log.info("Request to display current user: {}", currentLogin);
        RedmineUserDetails redmineUser = this.getCurrentRedmineUser(currentUserBasicAuth);
        if (redmineUser == null) {
            log.info("Failed to display as current user: {}", currentLogin);
            return Response.status(403).build();
        } else {
            log.info("User accessed as current: {}", currentLogin);
            return Response.ok(this.mapRedmineUser(redmineUser)).build();
        }
    }

    private UserDetails mapRedmineUser(final RedmineUserDetails redmineUser) {
        return new UserDetails(redmineUser.login(),
                               redmineUser.firstname().equals(EMPTY) ? null : redmineUser.firstname(),
                               redmineUser.lastname().equals(EMPTY) ? null : redmineUser.lastname(),
                               redmineUser.email(),
                               redmineUser.icon());
    }

    @PUT
    @Path("/me")
    @Consumes(APPLICATION_JSON)
    public Response updateMe(UserCreate newUser) {
        System.out.println("update me");
        return null;
    }

    @DELETE
    @Path("/me")
    public Response deleteMe(final @HeaderParam(AUTHORIZATION) String currentUserBasicAuth) {
        String currentLogin = this.decodeBasicAuthLogin(currentUserBasicAuth);
        if (currentLogin == null) {
            log.info("Got delete request for current user with invalid Auth header value: {}",
                     currentUserBasicAuth);
            return Response.status(403).build();
        }

        log.info("Request to delete current user: {}", currentLogin);
        RedmineUserDetails redmineUser = this.getCurrentRedmineUser(currentUserBasicAuth);
        String deleteUserUri = format("%susers/%d.json",
                                      this.redmineUrl,
                                      redmineUser.id());
        WebTarget target = this.restClient.target(deleteUserUri);
        Response redmineResponse = target.request()
                      .header("X-Redmine-API-Key", this.redmineKey)
                                         .buildDelete()
                                         .invoke();
        if (redmineResponse.getStatus() == 200) {
            log.info("User was deleted in Redmine");
            redmineResponse.close();
            return Response.noContent().build();
        } else {
            return Response.status(403).build();
        }
    }

    private RedmineUserDetails getCurrentRedmineUser(final String basicAuthHeaderValue) {
        WebTarget target = this.restClient.target(this.redmineUrl + "users/current.json");
        Response redmineResponse = target.request(APPLICATION_JSON)
                                         .header(AUTHORIZATION,
                                                 basicAuthHeaderValue)
                                         .buildGet()
                                         .invoke();
        if (redmineResponse.getStatus() == 401) {
            return null;
        } else {
            return redmineResponse.readEntity(RedmineUserDetailsCurrentWrapper.class).user();
        }
    }

    private String decodeBasicAuthLogin(final String authHeaderValue) {
        try {
            String loginPassAuthPart = authHeaderValue.split(" ")[1];
            return new String(Base64.getDecoder().decode(loginPassAuthPart)).split(":")[0];
        } catch (IndexOutOfBoundsException | IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

}
