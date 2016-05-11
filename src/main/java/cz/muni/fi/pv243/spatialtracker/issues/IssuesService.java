
package cz.muni.fi.pv243.spatialtracker.issues;

import cz.muni.fi.pv243.spatialtracker.ErrorReport;
import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.dto.RedmineIssueCreate;
import static javafx.scene.layout.BorderWidths.EMPTY;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
@Path("/issue")
public class IssuesService {

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
            final @Valid @NotNull IssueCreate newIssue,
            final @Context UriInfo issueApiLocation) {
        log.info("New issue creation reguest: {}", newIssue);
        RedmineIssueCreate redmineNewIssue = new RedmineIssueCreate();
        WebTarget target = this.restClient.target(this.redmineUrl + "issues.json");
        Response redmineResponse = target.request(APPLICATION_JSON)
                                         .header("X-Redmine-API-Key", this.redmineKey)
                                         .buildPost(json(redmineNewIssue))
                                         .invoke();
//        if (redmineResponse.getStatus() == 201) {
//            log.info("User was created in Redmine");
//            redmineResponse.close();
//            return Response.created(userApiLocation.getAbsolutePathBuilder()
//                                                   .path(newUser.login()).build())
//                           .build();
//        } else if (redmineResponse.getStatus() == 422) {
//            ErrorReport errors = redmineResponse.readEntity(ErrorReport.class);
//            log.info("Failed to create user: {}", errors);
//            return Response.status(400)
//                           .entity(errors)
//                           .build();
//        } else {
//            if (redmineResponse.hasEntity()) {
//                String rawRedmineResponse = redmineResponse.readEntity(String.class);
//                log.error("New user was not created in Redmine, "
//                          + "unknown response with status <{}> was received from Redmine: {}",
//                          redmineResponse.getStatus(), rawRedmineResponse);
//            }
//            //say sorry or something
//            return Response.serverError().build();
//        }
        return null;
    }


}
