package cz.muni.fi.pv243.spatialtracker.webchat.store;

import cz.muni.fi.pv243.spatialtracker.infinispan.CacheProvider;
import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import org.infinispan.Cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@ApplicationScoped
public class CacheWebChatMessageStore implements WebChatMessageStore {

	@Inject
	private CacheProvider cacheProvider;

	public void addMessage(String key, WebChatMessage message) {
		Cache<String, List<WebChatMessage>> messagesCache = cacheProvider.getMessageCache();
		List<WebChatMessage> messages = messagesCache.get(key);
		if (messages == null) {
			messages = new LinkedList<>();
		}
		messages.add(message);
		messagesCache.put(key, messages);
	}

	public List<WebChatMessage> getMessages(String key) {
		Cache<String, List<WebChatMessage>> messagesCache = cacheProvider.getMessageCache();
		List<WebChatMessage> messages = messagesCache.get(key);
		if (messages == null) {
			return new LinkedList<>();
		}
		return Collections.unmodifiableList(messages);
	}
}