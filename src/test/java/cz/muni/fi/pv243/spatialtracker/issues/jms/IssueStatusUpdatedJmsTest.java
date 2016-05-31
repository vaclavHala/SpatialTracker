package cz.muni.fi.pv243.spatialtracker.issues.jms;

import cz.muni.fi.pv243.spatialtracker.issues.IssueStatus;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import javax.inject.Inject;
import javax.jms.*;

@RunWith(Arquillian.class)
public class IssueStatusUpdatedJmsTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(javax.jms.JMSContext.class, javax.jms.JMSProducer.class, javax.jms.Message.class, javax.jms.Queue.class)
                .addPackages(true, IssueStatusUpdatedJms.class.getPackage())
                .addClasses(IssueStatus.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    private static final String TEST_QUEUE_NAME = "TestQueue";

    @Inject
    IssueStatusUpdatedJms jms;

    @Inject
    private JMSContext context;
    
    private String originalQueueName;

    @Before
    public void prepareQueue() {
        originalQueueName = IssueStatusUpdatedJms.QUEUE_NAME;
        IssueStatusUpdatedJms.QUEUE_NAME = TEST_QUEUE_NAME;

        Queue queue = context.createQueue(TEST_QUEUE_NAME);
        purgeQueue(queue);
    }

    private void purgeQueue(Queue q) {
        try (JMSConsumer consumer = context.createConsumer(q)) {
            Message m;
            do {
                m = consumer.receiveNoWait();
                if (m != null) {
                    try {
                        m.acknowledge();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            } while (m != null);
        }
    }

    @After
    public void setOriginalValues() {
        IssueStatusUpdatedJms.QUEUE_NAME = originalQueueName;
    }

    @Test
    public void messageIsSentToQueueTest() throws JMSException {
        Queue queue = context.createQueue(TEST_QUEUE_NAME);
        long ID = 1;
        IssueStatus status = IssueStatus.ACCEPTED;

        jms.issueUpdated(new IssueStatusUpdatedEvent(ID, status));

        try (JMSConsumer consumer = context.createConsumer(queue)) {
            Message m = consumer.receiveNoWait();
            assertNotNull(m);
            assertTrue(m instanceof ObjectMessage);
            ObjectMessage objectMessage = (ObjectMessage) m;
            IssueStatusUpdatedEvent event = (IssueStatusUpdatedEvent) objectMessage.getObject();
            assertEquals(ID, event.ID());
            assertEquals(status, event.status());
        }
    }
}