package cz.muni.fi.pv243.spatialtracker.issues.redmine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import cz.muni.fi.pv243.spatialtracker.SpatialTracker;
import static cz.muni.fi.pv243.spatialtracker.TestConstants.GAV_WIREMOCK;
import cz.muni.fi.pv243.spatialtracker.config.Config;
import cz.muni.fi.pv243.spatialtracker.common.SpatialTrackerException;
import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_PORT;
import cz.muni.fi.pv243.spatialtracker.issues.IssueService;
import cz.muni.fi.pv243.spatialtracker.issues.IssueStatus;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueStatus.ACCEPTED;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueStatus.REPORTED;
import cz.muni.fi.pv243.spatialtracker.issues.IssueStatusUpdateEvent;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueUpdateStatus;
import cz.muni.fi.pv243.spatialtracker.issues.redmine.dto.RedmineIssueDetails;
import cz.muni.fi.pv243.spatialtracker.issues.redmine.dto.RedmineIssueDetailsSingleWrapper;
import cz.muni.fi.pv243.spatialtracker.issues.redmine.dto.RedmineStatus;
import cz.muni.fi.pv243.spatialtracker.users.UserService;
import static java.lang.String.format;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import lombok.extern.slf4j.Slf4j;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@Slf4j
@RunWith(Arquillian.class)
public class RedmineIssueServiceTest {

    private static final String WIRE_SCENARIO = "SCENARIO";

    @Inject
    @Property(REDMINE_PORT)
    private int redminePort;

    private WireMockServer fakeRedmine;

    private ObjectMapper json = new ObjectMapper();

    @Inject
    private RedmineStatusMapper statusMapper;

    @Inject
    private RedmineIssueService sut;

    private static final AtomicReference<IssueStatusUpdateEvent> eventFired = new AtomicReference<>();

    @Before
    public void startFakeRedmine() {
        this.fakeRedmine = new WireMockServer(this.redminePort);
        this.fakeRedmine.start();

        eventFired.set(null);
    }

    @After
    public void stopFakeRedmine() {
        this.fakeRedmine.stop();
    }

    @Deployment
    public static WebArchive create() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, RedmineIssueServiceTest.class.getSimpleName() + ".war");
        war.addPackages(true,
                        IssueService.class.getPackage(),
                        UserService.class.getPackage(),
                        SpatialTrackerException.class.getPackage())
           .addPackages(false,
                        Config.class.getPackage(),
                        SpatialTracker.class.getPackage())
           .addAsLibraries(Maven.resolver()
                                .resolve(GAV_WIREMOCK)
                                .withTransitivity().asFile())
           .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
           //ids to map ids in filter are read from here
           .addAsResource("redmine-test.json", "redmine.json");
        return war;
    }

    @Test
    public void shouldEmitEventWhenIssueChangesState() throws Exception {
        String scenarioIssueUpdated = "UPDATED";
        long issueId = 999;
        String redmineIssueUrl = format("/issues/%d.json", issueId);
        IssueStatus initialStatus = REPORTED;
        IssueStatus targetStatus = ACCEPTED;

        //first we get initial state of issue
        RedmineIssueDetails redmineDetailsInitial =
                RedmineIssueDetails.builder()
                                   .status(new RedmineStatus(this.statusMapper.toId(initialStatus), ""))
                                   .build();
        String redmineDetailsInitialJson =
                this.json.writeValueAsString(new RedmineIssueDetailsSingleWrapper(redmineDetailsInitial));
        this.fakeRedmine.stubFor(WireMock.get(urlEqualTo(redmineIssueUrl)).inScenario(WIRE_SCENARIO)
                                         .whenScenarioStateIs(STARTED)
                                         .willReturn(aResponse().withStatus(200)
                                                                .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                                                                .withBody(redmineDetailsInitialJson)));

        //then sent the PUT with update
        this.fakeRedmine.stubFor(WireMock.put(urlEqualTo(redmineIssueUrl)).inScenario(WIRE_SCENARIO)
                                         .willSetStateTo(scenarioIssueUpdated)
                                         .willReturn(aResponse().withStatus(200)));

        //and check whether the update worked
        RedmineIssueDetails redmineDetailsUpdated =
                RedmineIssueDetails.builder()
                                   .status(new RedmineStatus(this.statusMapper.toId(targetStatus), ""))
                                   .build();
        String redmineDetailsUpdatedJson =
                this.json.writeValueAsString(new RedmineIssueDetailsSingleWrapper(redmineDetailsUpdated));
        this.fakeRedmine.stubFor(WireMock.get(urlMatching(redmineIssueUrl)).inScenario(WIRE_SCENARIO)
                                         .whenScenarioStateIs(scenarioIssueUpdated)
                                         .willReturn(aResponse().withStatus(200)
                                                                .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                                                                .withBody(redmineDetailsUpdatedJson)));

        this.sut.updateIssueState(issueId, targetStatus);

        assertEquals(issueId, eventFired.get().issueId());
        assertEquals(initialStatus, eventFired.get().previousStatus());
        assertEquals(targetStatus, eventFired.get().currentStatus());
    }

    public void observeIssueUpdateEvent(final @Observes IssueStatusUpdateEvent event){
        eventFired.set(event);
    }

    @Test
    public void shouldNotEmitEventOnFailedIssueStatusUpdate() throws Exception {
        long issueId = 999;
        String redmineIssueUrl = format("/issues/%d.json", issueId);

        this.fakeRedmine.stubFor(WireMock.get(urlEqualTo(redmineIssueUrl))
                                         .willReturn(aResponse().withStatus(404)));

        try {
            this.sut.updateIssueState(issueId, ACCEPTED);
        } catch (SpatialTrackerException e) {
            //expected
        }

        assertTrue(eventFired.get() == null);
    }
}
