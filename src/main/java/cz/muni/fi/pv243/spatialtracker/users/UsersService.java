package cz.muni.fi.pv243.spatialtracker.users;

import cz.muni.fi.pv243.spatialtracker.ErrorReport;
import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserCreate;
import cz.muni.fi.pv243.spatialtracker.users.dto.RedmineUserCreate;
import cz.muni.fi.pv243.spatialtracker.users.dto.RedmineUsersDetails;
import cz.muni.fi.pv243.spatialtracker.users.dto.RedmineUsersDetails.UserMatch;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserDetails;
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
    public Response details(final @PathParam("login") String forLogin) {
        log.info("Request to display user: {}", forLogin);
        WebTarget target = this.restClient.target(this.redmineUrl + "users.json")
                                          .queryParam("name", forLogin)
                                          .queryParam("limit", 1);
        RedmineUsersDetails redmineResponse = target.request(APPLICATION_JSON)
                                                    .header("X-Redmine-API-Key", this.redmineKey)
                                                    .buildGet()
                                                    .invoke(RedmineUsersDetails.class);
        UserDetails user = this.extractUser(forLogin, redmineResponse.matchedUsers());
        if (user != null) {
            log.info("User <{}> was found", forLogin);
            return Response.ok(user).build();
        } else {
            log.info("User <{}> does not exist", forLogin);
            return Response.status(404).build();
        }
    }

    private UserDetails extractUser(final String login, final List<UserMatch> foundUsers) {
        for (UserMatch match : foundUsers) {
            if (match.login().equals(login)) {
                return new UserDetails(match.login(),
                                       match.firstname().equals(EMPTY) ? null : match.firstname(),
                                       match.lastname().equals(EMPTY) ? null : match.lastname(),
                                       match.email(),
                                       match.icon());
            }
        }
        return null;
    }

    @GET
    @Path("/me")
    public Response details() {
        System.out.println("display current user");
        return null;
    }

    @PUT
    @Path("/me")
    @Consumes(APPLICATION_JSON)
    public Response update(UserCreate newUser) {
        System.out.println("update me");
        return null;
    }

    @DELETE
    @Path("/me")
    public Response delete() {
        System.out.println("delete me");
        return null;
    }

}
