package cz.muni.fi.pv243.spatialtracker.infinispan;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import java.io.IOException;

/**
 * @author opontes
 */
public class MessageService {
    private final EmbeddedCacheManager cacheManager;
    private Cache<String, WebChatMessage> cache;


    public MessageService() throws IOException {
        cacheManager = new DefaultCacheManager("infinispan.xml");
        cache = cacheManager.getCache();
    }
}
