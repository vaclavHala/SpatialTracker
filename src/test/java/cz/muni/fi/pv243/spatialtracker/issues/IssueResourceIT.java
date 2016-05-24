package cz.muni.fi.pv243.spatialtracker.issues;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import cz.muni.fi.pv243.spatialtracker.SpatialTracker;
import cz.muni.fi.pv243.spatialtracker.config.Config;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.ADD;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.REMOVE;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.REPAIR;
import static cz.muni.fi.pv243.spatialtracker.issues.IssuePriority.CAN_HAVE;
import static cz.muni.fi.pv243.spatialtracker.issues.IssuePriority.MUST_HAVE;
import static cz.muni.fi.pv243.spatialtracker.issues.IssuePriority.SHOULD_HAVE;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueStatus.REPORTED;
import cz.muni.fi.pv243.spatialtracker.issues.dto.Coordinates;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsFull;
import static cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.assembleBasicAuthHeader;
import cz.muni.fi.pv243.spatialtracker.users.UserResource;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserCreate;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import static java.util.stream.Collectors.toList;
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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@Slf4j
@RunAsClient
@RunWith(Arquillian.class)
public class IssueResourceIT {

    private static final TypeReference<List<IssueDetailsBrief>> ISSUE_DETAILS_LIST_TOKEN =
            new TypeReference<List<IssueDetailsBrief>>() {};

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
           //to be able to log in we need these config bits
           .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"), "web.xml")
           .addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-web.xml"), "jboss-web.xml")
           //for tests redmine URL is shifted by 1 to not clash with concurrently running live redmine
           .addAsResource("redmine-test.json", "redmine.json");
        return war;
    }

    @Before
    public void initDb() throws SQLException, InterruptedException, IOException {
        //if we dont stop redmine it will be impossible to drop the db
        cube.stop("wf-with-red");
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
        cube.start("wf-with-red");
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
    public void shouldNotAllowAnnonymousUserToReportIssue(
            final @ArquillianResource URL appUrl) throws Exception {
        String issueApiUrl = appUrl + "rest/issue/";
        IssueCreate newIssue = new IssueCreate("Stuffs' broke", null, MUST_HAVE, REPAIR, new Coordinates(12.3, 4.56));
        HttpResponse<String> respReport =
                Unirest.post(issueApiUrl)
                       .header(CONTENT_TYPE, APPLICATION_JSON)
                       .header(ACCEPT, APPLICATION_JSON)
                       .body(this.json.writeValueAsString(newIssue))
                       .asString();
        assertThat(respReport.getStatus()).isEqualTo(403);
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

    @Test
    public void shouldFilterIssuesUsingAllGivenFilters(
            final @ArquillianResource URL appUrl) throws Exception {

        String login = "reporter";
        String pass = "sneaky";
        Unirest.post(appUrl + "rest/user/")
               .header(CONTENT_TYPE, APPLICATION_JSON)
               .body(this.json.writeValueAsString(new UserCreate(login, pass, "mail@me.now")))
               .asString();

        List<IssueCreate> allIssues = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/testdata/issues_100.json")))) {
            String jsonIssue = null;
            while ((jsonIssue = reader.readLine()) != null) {
                IssueCreate issue = this.json.readValue(jsonIssue, IssueCreate.class);
                allIssues.add(issue);
                HttpResponse<String> resp =
                        Unirest.post(appUrl + "rest/issue/")
                               .basicAuth(login, pass)
                               .header(CONTENT_TYPE, APPLICATION_JSON)
                               .body(this.json.writeValueAsString(issue))
                               .asString();
                assertEquals(201, resp.getStatus());
            }
        }

        List<IssueDetailsBrief> expectedIssues =
                allIssues.stream()
                         .filter(i -> i.category().equals(ADD) || i.category().equals(REMOVE))
                         .filter(i -> i.priority().equals(CAN_HAVE) || i.priority().equals(SHOULD_HAVE) || i.priority().equals(MUST_HAVE))
                         .filter(i -> i.coords().latitude() >= 3 && i.coords().latitude() <= 6)
                         .filter(i -> i.coords().longitude() >= 4 && i.coords().longitude() <= 7)
                         .map(i -> new IssueDetailsBrief(0, i.subject(), i.coords()))
                         .collect(toList());

        String filter = "[" +
                        "{\"@type\":\"category\",\"in\":[\"ADD\",\"REMOVE\"]}," +
                        "{\"@type\":\"priority\",\"min\":\"CAN_HAVE\",\"max\":\"MUST_HAVE\"}," +
                        "{\"@type\":\"spatial\",\"lat_min\":3,\"lat_max\":6,\"lon_min\":4,\"lon_max\":7}" +
                        "]";
        String requestWithFilter = appUrl + "rest/issue?filter=" + URLEncoder.encode(filter, "UTF-8");
        HttpResponse<String> respFind =
                Unirest.get(requestWithFilter)
                       .header(ACCEPT, APPLICATION_JSON)
                       .asString();
        List<IssueDetailsBrief> filteredIssues = this.json.readValue(respFind.getBody(), ISSUE_DETAILS_LIST_TOKEN);

        assertThat(filteredIssues).usingElementComparatorOnFields("subject", "coords")
                                  .containsOnlyElementsOf(expectedIssues);
    }

}
