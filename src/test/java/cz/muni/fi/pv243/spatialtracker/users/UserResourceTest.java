package cz.muni.fi.pv243.spatialtracker.users;

import cz.muni.fi.pv243.spatialtracker.SpatialTracker;
import cz.muni.fi.pv243.spatialtracker.config.Config;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserCreate;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserDetails;
import cz.muni.fi.pv243.spatialtracker.users.redmine.RedminUsersService;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
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
@RunWith(Arquillian.class)
public class UserResourceTest {

    @Deployment//(testable = false)
    public static WebArchive create() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, UserResourceTest.class.getSimpleName() + ".war");
        war.addPackages(true, UserResource.class.getPackage(),
                        Config.class.getPackage())
           .addPackages(false, SpatialTracker.class.getPackage())
           .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return war;
    }

    @Inject
    private RedminUsersService usersService;

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
    public void shouldFindUserByLoginAfterRegistering() throws Exception {
        String login = "someName";
        UserCreate newUser = new UserCreate(login, "secret", "mail@me.now");
        String newUserResource = this.usersService.register(newUser);
        UserDetails foundUser = this.usersService.detailsSomeUser(newUserResource).get();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundUser.login()).isEqualTo(newUser.login());
        softly.assertThat(foundUser.email()).isEqualTo(newUser.email());
        softly.assertThat(foundUser.firstname()).isNullOrEmpty();
        softly.assertThat(foundUser.lastname()).isNullOrEmpty();
        softly.assertAll();
    }

}
