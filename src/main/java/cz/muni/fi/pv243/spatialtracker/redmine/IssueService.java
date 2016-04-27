package cz.muni.fi.pv243.spatialtracker.redmine;

import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import cz.muni.fi.pv243.spatialtracker.redmine.dto.*;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
public class IssueService {

    @Inject
    @Property(REDMINE_API_KEY)
    private String redmineKey;
    @Inject
    @Property(REDMINE_BASE_URL)
    private String redmineUrl;

    @Inject
    private Client restClient;

    @Inject
    private Validator validator;

    @GET
    @Path("foo")
    public void foo(Coordinates coords) {
        System.out.println(coords);
    }

    @GET
    @Path("bar")
    @Produces(APPLICATION_JSON)
    public Coordinates barGet() {
        //        return new Coordinates(1, 2);
        return this.restClient.target("http://127.0.0.1:8080/tracker/rest/issue/bar")
                              .request()
                              .header("X-Redmine-API-Key", this.redmineKey)
                              .buildPost(Entity.json(new Coordinates(12.3, 4.56)))
                              .invoke(Coordinates.class);
    }

    @POST
    @Path("bar")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Coordinates barPost(Coordinates c) {
        return c;
    }

    @POST
    //    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void createIssue(final CreateIssue issue) {
        this.validator.validate(issue);

        String response =
                this.restClient.target(this.redmineUrl + "issues.json")
                               .request(APPLICATION_JSON)
                               .header("X-Redmine-API-Key", this.redmineKey)
                               .buildPost(Entity.json(issue))
                               .invoke(String.class);
        log.info(response);
    }

    @GET
    @Produces(APPLICATION_JSON)
    public void listIssues() {
        String issues = this.restClient.target(this.redmineUrl + "projects.json")
                                       .request(APPLICATION_JSON)
                                       .header("X-Redmine-API-Key", this.redmineKey)
                                       .buildGet()
                                       .invoke(String.class);
        log.info("issues {}", issues);
    }
}
