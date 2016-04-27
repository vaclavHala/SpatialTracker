package cz.muni.fi.pv243.spatialtracker.redmine;

import javax.inject.Inject;
import javax.validation.Validator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import cz.muni.fi.pv243.spatialtracker.redmine.dto.CreateProject;
import cz.muni.fi.pv243.spatialtracker.redmine.dto.Projects;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@Path("/project")
public class ProjectService {

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


//    @GET
//    @Produces(APPLICATION_JSON)
    public void createProject(final CreateProject project) {
        this.validator.validate(project);

        WebTarget target = this.restClient.target(this.redmineUrl + "projects.json");

        String response = target.request(APPLICATION_JSON)
                                .header("X-Redmine-API-Key", this.redmineKey)
                                .buildPost(Entity.json(project))
                                .invoke(String.class);
        log.info(response);
    }


    @GET
    @Produces(APPLICATION_JSON)
    public Projects listProjects() {
        WebTarget target = this.restClient.target(this.redmineUrl + "projects.json");

        return target.request(APPLICATION_JSON)
                                .header("X-Redmine-API-Key", this.redmineKey)
                                .buildGet()
                                .invoke(Projects.class);
    }
}
