package cz.muni.fi.pv243.spatialtracker.webchat.store;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import org.infinispan.commons.api.BasicCache;

@ApplicationScoped
public class WebChatMessageStore {

	@Inject
	private CacheContainerProvider cacheProvider;

	private static final String MESSAGES_CACHE_NAME = "messages";

	public void addMessage(String key, WebChatMessage message) {
		BasicCache<String, Object> messagesCache = getMessagesCache();
		List<WebChatMessage> messages;
		synchronized (this) {
			messages = (List<WebChatMessage>) messagesCache.get(key);
			if (messages == null) {
				messages = new LinkedList<>();
				messagesCache.put(key, messages);
			}
			messages.add(message);
		}
	}

	public List<WebChatMessage> getMessages(String key) {
		BasicCache<String, Object> messagesCache = getMessagesCache();
		List<WebChatMessage> messages;
		synchronized (this) {
			messages = (List<WebChatMessage>) messagesCache.get(key);
		}
		if (messages == null) {
			return new LinkedList<>();
		}
		return messages;
	}

	BasicCache<String, Object> getMessagesCache() {
		return cacheProvider.getCacheContainer().getCache(MESSAGES_CACHE_NAME);
	}
}