package cz.muni.fi.pv243.spatialtracker.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import cz.muni.fi.pv243.spatialtracker.SpatialTracker;
import cz.muni.fi.pv243.spatialtracker.config.Config;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserCreate;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserDetails;
import java.net.URL;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.assertj.core.api.SoftAssertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
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
@RunAsClient
@RunWith(Arquillian.class)
public class UserResourceIT {

    private final ObjectMapper json = new ObjectMapper();

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
    public void shouldFindRegisteredUser(            final @ArquillianResource URL appUrl) throws Exception {
        String userApiUrl = appUrl + "rest/user/";
        UserCreate newUser = new UserCreate("someName", "secret", "mail@me.now");

        HttpResponse<String> respRegister =
                Unirest.post(userApiUrl)
                       .header(CONTENT_TYPE, APPLICATION_JSON)
                       .body(this.json.writeValueAsString(newUser))
                       .asString();
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


}
