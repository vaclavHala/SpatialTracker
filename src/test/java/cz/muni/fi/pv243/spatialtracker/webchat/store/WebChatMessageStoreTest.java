package cz.muni.fi.pv243.spatialtracker.webchat.store;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.infinispan.commons.api.BasicCache;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import org.junit.runner.RunWith;

import javax.inject.Inject;

//TODO: requires proper configuration of tests using Infinispan
//@RunWith(Arquillian.class)
public class WebChatMessageStoreTest {

	//@Deployment
	public static JavaArchive createDeployment() {
		return ShrinkWrap.create(JavaArchive.class)
				.addClass(TestCacheContainerProvider.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	//@Inject
	WebChatMessageStore store;

	//@Test
	public void addMessageTest() {
		addSampleKeyMessages();

		assertSampleKeyMessages();
	}

	//@Test
	public void getMessagesTest() {
		List<WebChatMessage> msgs = store.getMessages(sampleKey);

		assertEquals(msgs.size(), 0);
	}

	//@Test
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

		assertEquals(msgs.size(), 2);

		WebChatMessage msg = msgs.get(0);
		assertEquals(msg.name(), sampleName1);
		assertEquals(msg.text(), sampleText1);

		msg = msgs.get(1);
		assertEquals(msg.name(), sampleName2);
		assertEquals(msg.text(), sampleText2);
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
		assertEquals(msg.name(), differentName);
		assertEquals(msg.text(), differentText);
	}
}
