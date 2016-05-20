package cz.muni.fi.pv243.spatialtracker.webchat.store;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;

@ApplicationScoped
public class WebChatMessageStore {
	private Map<String, List<WebChatMessage>> keyToMessage = new HashMap<>();

	public void addMessage(String key, WebChatMessage message) {
		List<WebChatMessage> sessions;
		synchronized (this) {
			sessions = keyToMessage.get(key);
			if (sessions == null) {
				sessions = new LinkedList<WebChatMessage>();
				keyToMessage.put(key, sessions);
			}
			sessions.add(message);
		}
	}

	public List<WebChatMessage> getMessages(String key) {
		List<WebChatMessage> msgs;
		synchronized (this) {
			msgs = keyToMessage.get(key);
		}
		if (msgs == null) {
			return new LinkedList<WebChatMessage>();
		}
		return msgs;
	}
}