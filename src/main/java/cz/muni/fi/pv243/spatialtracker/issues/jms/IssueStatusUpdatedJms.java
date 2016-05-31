package cz.muni.fi.pv243.spatialtracker.issues.jms;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;

@Stateless
public class IssueStatusUpdatedJms {

    public static String QUEUE_NAME = "IssueStatusUpdatedQueue";

    @Inject
    private JMSContext context;

    public void issueUpdated(@Observes IssueStatusUpdatedEvent e) {
        Queue queue = context.createQueue(QUEUE_NAME);
        JMSProducer producer = context.createProducer();
        Message m = context.createObjectMessage(e);
        producer.send(queue, m);
    }
}
