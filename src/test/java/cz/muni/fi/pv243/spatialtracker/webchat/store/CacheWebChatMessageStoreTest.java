package cz.muni.fi.pv243.spatialtracker.webchat.store;

import cz.muni.fi.pv243.spatialtracker.infinispan.CacheProvider;
import cz.muni.fi.pv243.spatialtracker.webchat.WebChatService;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import cz.muni.fi.pv243.spatialtracker.webchat.model.NewWebChatMessage;
import org.infinispan.commons.api.BasicCache;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Before;
import org.junit.Test;
import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

@RunWith(Arquillian.class)
public class CacheWebChatMessageStoreTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(CacheProvider.class, WebChatMessageStore.class,
                        CacheWebChatMessageStore.class)
                .addPackages(false, WebChatService.class.getPackage(), NewWebChatMessage.class.getPackage())
                .addAsLibraries(Maven.resolver()
                        .resolve("org.infinispan:infinispan-core:8.2.2.Final")
                        .withTransitivity().asFile())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private CacheWebChatMessageStore store;

    @Inject
    private CacheProvider cacheProvider;

    @Before
    public void setUp() {
        cacheProvider.getMessageCache().clear();
    }

    @Test
    public void addMessageTest() {
        addSampleKeyMessages();

        assertSampleKeyMessages();
    }

    @Test
    public void getMessagesTest() {
        List<WebChatMessage> msgs = store.getMessages(sampleKey);

        assertEquals(0, msgs.size());
    }

    @Test
    public void addMessagesWithDifferentKeyTest() {
        addSampleKeyMessages();
        addDifferentKeyMessages();

        assertSampleKeyMessages();
        assertDifferentKeyMessages();
    }

    private final String sampleKey = "someKey";

    private final String sampleName1 = "John Doe";
    private final String sampleText1 = "first message";

    private final String sampleName2 = "Second John Doe";
    private final String sampleText2 = "second message";

    private void addSampleKeyMessages() {
        store.addMessage(sampleKey, new WebChatMessage(sampleName1, sampleText1, new Date()));
        store.addMessage(sampleKey, new WebChatMessage(sampleName2, sampleText2, new Date()));
    }

    private void assertSampleKeyMessages() {
        List<WebChatMessage> msgs = store.getMessages(sampleKey);

        assertEquals(2, msgs.size());

        WebChatMessage msg = msgs.get(0);
        assertEquals(sampleName1, msg.name());
        assertEquals(sampleText1, msg.text());

        msg = msgs.get(1);
        assertEquals(sampleName2, msg.name());
        assertEquals(sampleText2, msg.text());
    }

    private final String differentKey = "different key";

    private final String differentName = "James Bond";
    private final String differentText = "different text";

    private void addDifferentKeyMessages() {
        store.addMessage(differentKey, new WebChatMessage(differentName, differentText, new Date()));
    }

    private void assertDifferentKeyMessages() {
        List<WebChatMessage> msgs = store.getMessages(differentKey);

        assertEquals(msgs.size(), 1);

        WebChatMessage msg = msgs.get(0);
        assertEquals(differentName, msg.name());
        assertEquals(differentText, msg.text());
    }
}
