package cz.muni.fi.pv243.spatialtracker.issues;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import cz.muni.fi.pv243.spatialtracker.SpatialTracker;
import cz.muni.fi.pv243.spatialtracker.config.Config;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.ADD;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.REPAIR;
import static cz.muni.fi.pv243.spatialtracker.issues.IssuePriority.MUST_HAVE;
import static cz.muni.fi.pv243.spatialtracker.issues.IssuePriority.SHOULD_HAVE;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueStatus.REPORTED;
import cz.muni.fi.pv243.spatialtracker.issues.dto.Coordinates;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsFull;
import static cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.assembleBasicAuthHeader;
import cz.muni.fi.pv243.spatialtracker.users.UserResource;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserCreate;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import static java.util.Arrays.asList;
import java.util.List;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import lombok.extern.slf4j.Slf4j;
import org.arquillian.cube.CubeController;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.SoftAssertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@Slf4j
@RunAsClient
@RunWith(Arquillian.class)
public class IssueResourceIT {

    private final ObjectMapper json = new ObjectMapper();

    @ArquillianResource
    private CubeController cube;

    @Deployment(testable = false)
    public static WebArchive create() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, IssueResourceIT.class.getSimpleName() + ".war");
        war.addPackages(true,
                        IssueResource.class.getPackage(),
                        UserResource.class.getPackage(),
                        Config.class.getPackage())
           .addPackages(false,
                        SpatialTracker.class.getPackage())
           .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
           //for tests redmine URL is shifted by 1 to not clash with concurrently running live redmine
           .addAsResource(new StringAsset("{\"base_url\" : \"http://127.0.0.1:3001/\"," +
                                          "\"api_key\" : \"264acfed33b8af628991dda4de64d75390854d82\"}"),
                          "redmine.json");
        return war;
    }

    @Before
    public void initDb() throws SQLException, InterruptedException, IOException {
        //if we dont stop redmine it will be impossible to drop the db
        cube.stop("redmine-aq");
        //connect to the container with postgres, in the container execute sql script which recreates redmine db
        //docker and this address are used to run the test, so it is reasonably safe to assume it will work fine
        List<String> cleanupScript =
                asList("docker", "-H", "tcp://127.0.0.1:2375", "exec", "postgres-aq",
                       "psql", "-U", "redmine", "-f", "/docker-entrypoint-initdb.d/redmine.sql");
        Process cleanup = new ProcessBuilder(cleanupScript).start();
        //turn these on to see output of psql command
        //.redirectError(Redirect.INHERIT)
        //.redirectOutput(Redirect.INHERIT)

        log.info("Cleanup exited: {}", cleanup.waitFor());
        cleanup.destroy();
        cube.start("redmine-aq");
    }

    @Test
    public void shouldAllowRegisteredUserToReportIssue(
            final @ArquillianResource URL appUrl) throws Exception {

        String issueApiUrl = appUrl + "rest/issue/";
        String userApiUrl = appUrl + "rest/user/";
        String login = "reporter";
        String pass = "sneaky";
        Unirest.post(userApiUrl)
               .header(CONTENT_TYPE, APPLICATION_JSON)
               .body(this.json.writeValueAsString(new UserCreate(login, pass, "mail@me.now")))
               .asString();

        IssueCreate newIssue = new IssueCreate("Stuffs' broke",
                                               null,
                                               MUST_HAVE,
                                               REPAIR,
                                               new Coordinates(12.3, 4.56));
        String basicAuth = assembleBasicAuthHeader(login, pass);
        HttpResponse<String> respReport =
                Unirest.post(issueApiUrl)
                       .header(CONTENT_TYPE, APPLICATION_JSON)
                       .header(ACCEPT, APPLICATION_JSON)
                       .header(AUTHORIZATION, basicAuth)
                       .body(this.json.writeValueAsString(newIssue))
                       .asString();
        assertThat(respReport.getStatus()).isEqualTo(201);
    }

    @Test
    public void shouldShowDetailsOfExistingIssue(
            final @ArquillianResource URL appUrl) throws Exception {
        String issueApiUrl = appUrl + "rest/issue/";
        String userApiUrl = appUrl + "rest/user/";
        String login = "reporter";
        String pass = "sneaky";
        Unirest.post(userApiUrl)
               .header(CONTENT_TYPE, APPLICATION_JSON)
               .body(this.json.writeValueAsString(new UserCreate(login, pass, "mail@me.now")))
               .asString();

        String subject = "subject";
        String description = "description";
        IssuePriority priority = SHOULD_HAVE;
        IssueCategory category = ADD;
        double latitude = 12.3;
        double longitude = 23.45;
        IssueCreate newIssue = new IssueCreate(subject,
                                               description,
                                               priority,
                                               category,
                                               new Coordinates(latitude, longitude));
        String basicAuth = assembleBasicAuthHeader(login, pass);
        HttpResponse<String> respReport =
                Unirest.post(issueApiUrl)
                       .header(CONTENT_TYPE, APPLICATION_JSON)
                       .header(ACCEPT, APPLICATION_JSON)
                       .header(AUTHORIZATION, basicAuth)
                       .body(this.json.writeValueAsString(newIssue))
                       .asString();
        String newIssueLocation = respReport.getHeaders().getFirst(LOCATION);

        HttpResponse<String> respFind =
                Unirest.get(newIssueLocation)
                       .header(ACCEPT, APPLICATION_JSON)
                       .asString();
        IssueDetailsFull foundIssue = this.json.readValue(respFind.getBody(), IssueDetailsFull.class);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundIssue.subject()).isEqualTo(subject);
        softly.assertThat(foundIssue.description()).isEqualTo(description);
        softly.assertThat(foundIssue.authorLogin()).isEqualTo(login);
        softly.assertThat(foundIssue.category()).isEqualTo(category);
        softly.assertThat(foundIssue.priority()).isEqualTo(priority);
        softly.assertThat(foundIssue.status()).isEqualTo(REPORTED);
        softly.assertThat(foundIssue.startedDate()).isToday(); //could blow up if run close to midnight
        softly.assertThat(foundIssue.coords().latitude()).isEqualTo(latitude);
        softly.assertThat(foundIssue.coords().longitude()).isEqualTo(longitude);
        softly.assertAll();
    }
}
