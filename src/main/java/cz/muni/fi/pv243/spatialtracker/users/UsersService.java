package cz.muni.fi.pv243.spatialtracker.users;

import cz.muni.fi.pv243.spatialtracker.ErrorReport;
import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import cz.muni.fi.pv243.spatialtracker.users.dto.CreateUser;
import cz.muni.fi.pv243.spatialtracker.users.dto.RedmineCreateUser;
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
import javax.ws.rs.core.UriBuilder;
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
            final @Valid @NotNull CreateUser newUser,
            final @Context UriInfo userApiLocation) {
        log.info("New user registration reguest: {}", newUser);
        RedmineCreateUser redmineNewUser = new RedmineCreateUser(newUser.login(),
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
    public Response details(@PathParam("login") String forLogin) {
        System.out.println("display " + forLogin);
        return null;
    }

    @GET
    @Path("/me")
    @Produces(APPLICATION_JSON)
    public Response details(final @Valid CreateUser newUser) {
        System.out.println("display current user");
        return null;
    }

    @PUT
    @Path("/me")
    @Consumes(APPLICATION_JSON)
    public Response update(CreateUser newUser) {
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
