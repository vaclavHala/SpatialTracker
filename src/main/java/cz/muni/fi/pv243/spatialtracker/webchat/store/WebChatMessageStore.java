package cz.muni.fi.pv243.spatialtracker.webchat.store;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import cz.muni.fi.pv243.spatialtracker.infinispan.CacheProvider;
import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;

@ApplicationScoped
public class WebChatMessageStore {

    private Cache<String, List<WebChatMessage>> messagesCache;
    private List<WebChatMessage> messages;

    @Inject
    private CacheProvider cacheProvider;

    public void addMessage(String key, WebChatMessage message) {
        messagesCache = cacheProvider.getMessageCache();
        messages = messagesCache.get(key);

        if (messages == null) {
            messages = new LinkedList<>();
            messagesCache.put(key, messages);
        }
        messages.add(message);
    }

    public List<WebChatMessage> getMessages(String key) {
        messagesCache = cacheProvider.getMessageCache();
        messages = messagesCache.get(key);

        if (messages == null) {
            return new LinkedList<>();
        }
        return messages;
    }
}