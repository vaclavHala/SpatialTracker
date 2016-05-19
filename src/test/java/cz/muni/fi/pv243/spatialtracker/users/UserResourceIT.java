package cz.muni.fi.pv243.spatialtracker.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import cz.muni.fi.pv243.spatialtracker.SpatialTracker;
import cz.muni.fi.pv243.spatialtracker.config.Config;
import static cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.assembleBasicAuthHeader;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserCreate;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserDetails;
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
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Arquillian Cube is broken, clean flag is not read from the config.
 * That means the containers (postgres-aq and redmine-aq)
 * need to be removed before each test run manually if something breaks:
 * docker rm -f postgres-aq redmine-aq
 *
 * Fix for this bug was already commited to aq repo but has not yet
 * propagated to released version :(
 */
@Slf4j
@RunAsClient
@RunWith(Arquillian.class)
public class UserResourceIT {

    private final ObjectMapper json = new ObjectMapper();

    @ArquillianResource
    private CubeController cube;

    @Deployment(testable = false)
    public static WebArchive create() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, UserResourceIT.class.getSimpleName() + ".war");
        war.addPackages(true,
                        UserResource.class.getPackage(),
                        Config.class.getPackage())
           .addPackages(false,
                        SpatialTracker.class.getPackage())
           .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
        //                .addAsLibraries(Maven.resolver()
        //                                .resolve(ASSERTJ_GAV)
        //                                .withTransitivity()
        //                                .asFile())
        ;
        return war;
    }

    public static void main(String[] args) throws Exception {
        new UserResourceIT().initDb();
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

    //    @Inject
    //    private RedminUsersService usersService;

    //    @Test
    //    public void shouldReturnFullLocationOfRegisteredUser() throws Exception {
    ////        System.out.println("go to sleep");
    ////        Thread.sleep(20_000);
    //    }
    //
    //    @Test
    //    public void shouldReturnErrorIfLoginExistsWhenRegistering() throws Exception {
    //
    //    }

    @Test
    public void shouldFindRegisteredUser(final @ArquillianResource URL appUrl) throws Exception {
        String userApiUrl = appUrl + "rest/user/";
        UserCreate newUser = new UserCreate("someName", "secret", "mail@me.now");

        HttpResponse<String> respRegister =
                Unirest.post(userApiUrl)
                       .header(CONTENT_TYPE, APPLICATION_JSON)
                       .body(this.json.writeValueAsString(newUser))
                       .asString();
        assertThat(respRegister.getStatus()).isEqualTo(CREATED.getStatusCode());
        String newUserLocation = respRegister.getHeaders().getFirst(LOCATION);

        HttpResponse<String> respFind =
                Unirest.get(newUserLocation)
                       .header(ACCEPT, APPLICATION_JSON)
                       .asString();
        UserDetails foundUser = this.json.readValue(respFind.getBody(), UserDetails.class);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundUser.login()).isEqualTo(newUser.login());
        softly.assertThat(foundUser.email()).isEqualTo(newUser.email());
        softly.assertThat(foundUser.firstname()).isNullOrEmpty();
        softly.assertThat(foundUser.lastname()).isNullOrEmpty();
        softly.assertAll();
    }

    @Test
    public void shouldNotFindDeletedUser(final @ArquillianResource URL appUrl) throws Exception {
        String userApiUrl = appUrl + "rest/user/";
        String login = "Pepa";
        String pass = "wontTell";
        UserCreate newUser = new UserCreate(login, pass, "mail@me.now");

        HttpResponse<String> respRegister =
                Unirest.post(userApiUrl)
                       .header(CONTENT_TYPE, APPLICATION_JSON)
                       .body(this.json.writeValueAsString(newUser))
                       .asString();
        String newUserLocation = respRegister.getHeaders().getFirst(LOCATION);

        String basicAuthHeader = assembleBasicAuthHeader(login, pass);
        HttpResponse<String> respDelete =
                Unirest.delete(userApiUrl + "me")
                       .header(AUTHORIZATION, basicAuthHeader)
                       .asString();
        assertThat(respDelete.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());

        HttpResponse<String> respFoundUser =
                Unirest.get(newUserLocation)
                       .header(CONTENT_TYPE, APPLICATION_JSON)
                       .asString();
        assertThat(respFoundUser.getStatus()).isEqualTo(NOT_FOUND.getStatusCode());
    }
}
