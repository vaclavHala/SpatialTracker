package cz.muni.fi.pv243.spatialtracker.redmine;

import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import cz.muni.fi.pv243.spatialtracker.redmine.dto.CreateIssue;
import cz.muni.fi.pv243.spatialtracker.redmine.dto.CreateProject;
import cz.muni.fi.pv243.spatialtracker.redmine.dto.CustomFields;
import cz.muni.fi.pv243.spatialtracker.redmine.dto.CustomFields.CustomField;
import cz.muni.fi.pv243.spatialtracker.redmine.dto.Projects;
import static java.util.Arrays.asList;
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
@Path("/issue")
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

    //    @POST
    @GET
    @Produces(APPLICATION_JSON)
    //    @Consumes(APPLICATION_JSON)
    public void createIssue(/*final CreateIssue issue*/) {
        CreateIssue issue = new CreateIssue(4, 1, 1, 1, 1, "Subject",
                                            new CustomFields(asList(new CustomField(2, "2"),
                                                                    new CustomField(3, "3"))));

        this.validator.validate(issue);

        WebTarget target = this.restClient.target(this.redmineUrl + "issues.json");

        String response = target.request(APPLICATION_JSON)
                                .header("X-Redmine-API-Key", this.redmineKey)
                                .buildPost(Entity.json(issue))
                                .invoke(String.class);
        log.info(response);
    }

    //    @GET
    //    @Produces(APPLICATION_JSON)
    //    public Projects listProjects() {
    //        WebTarget target = this.restClient.target(this.redmineUrl + "projects.json");
    //
    //        return target.request(APPLICATION_JSON)
    //                                .header("X-Redmine-API-Key", this.redmineKey)
    //                                .buildGet()
    //                                .invoke(Projects.class);
    //    }
}
