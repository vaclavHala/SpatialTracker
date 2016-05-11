
package cz.muni.fi.pv243.spatialtracker.redmine.users.dto;

import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import cz.muni.fi.pv243.spatialtracker.redmine.dto.Projects;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@Path("/user")
public class UsersService {

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
    public Response register(final @Valid CreateUser newUser) {
//        WebTarget target = this.restClient.target(this.redmineUrl + "projects.json");
//
//        return target.request(APPLICATION_JSON)
//                                .header("X-Redmine-API-Key", this.redmineKey)
//                                .buildGet()
//                                .invoke(Projects.class);
        System.out.println("register "+newUser);
        return null;
    }

    @GET
    @Path("/{login}")
    @Produces(APPLICATION_JSON)
    public Response details(@PathParam("login") String forLogin) {
        System.out.println("display "+forLogin);
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
    public Response delete(){
        System.out.println("delete me");
        return null;
    }


}
