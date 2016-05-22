package cz.muni.fi.pv243.spatialtracker.webchat.store;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;

public class WebChatMessageStoreTest {

	@Test
	public void addMessageTest() {
		WebChatMessageStore store = new WebChatMessageStore();

		addSampleKeyMessages(store);

		assertSampleKeyMessages(store);
	}

	@Test
	public void getMessagesTest() {
		WebChatMessageStore store = new WebChatMessageStore();

		List<WebChatMessage> msgs = store.getMessages(sampleKey);

		assertEquals(msgs.size(), 0);
	}

	@Test
	public void addMessagesWithDifferentKeyTest() {
		WebChatMessageStore store = new WebChatMessageStore();

		addSampleKeyMessages(store);
		addDifferentKeyMessages(store);

		assertSampleKeyMessages(store);
		assertDifferentKeyMessages(store);
	}

	private final String sampleKey = "someKey";

	private final String sampleName1 = "John Doe";
	private final String sampleText1 = "first message";

	private final String sampleName2 = "Second John Doe";
	private final String sampleText2 = "second message";

	private void addSampleKeyMessages(WebChatMessageStore store) {
		store.addMessage(sampleKey, new WebChatMessage(sampleName1, sampleText1));
		store.addMessage(sampleKey, new WebChatMessage(sampleName2, sampleText2));
	}

	private void assertSampleKeyMessages(WebChatMessageStore store) {
		List<WebChatMessage> msgs = store.getMessages(sampleKey);

		assertEquals(msgs.size(), 2);

		WebChatMessage msg = msgs.get(0);
		assertEquals(msg.getName(), sampleName1);
		assertEquals(msg.getText(), sampleText1);

		msg = msgs.get(1);
		assertEquals(msg.getName(), sampleName2);
		assertEquals(msg.getText(), sampleText2);
	}

	private final String differentKey = "different key";

	private final String differentName = "James Bond";
	private final String differentText = "different text";

	private void addDifferentKeyMessages(WebChatMessageStore store) {
		store.addMessage(differentKey, new WebChatMessage(differentName, differentText));
	}

	private void assertDifferentKeyMessages(WebChatMessageStore store) {
		List<WebChatMessage> msgs = store.getMessages(differentKey);

		assertEquals(msgs.size(), 1);

		WebChatMessage msg = msgs.get(0);
		assertEquals(msg.getName(), differentName);
		assertEquals(msg.getText(), differentText);
	}
}