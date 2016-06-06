package cz.muni.fi.pv243.spatialtracker.webchat.store;

import cz.muni.fi.pv243.spatialtracker.infinispan.CacheProvider;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestMessagesCacheProvider implements CacheProvider {

    private EmbeddedCacheManager manager;

    private static final String MESSAGE_CACHE = "messages-test";

    public Cache<String, IssueDetailsBrief> getIssueCache() {
        throw new UnsupportedOperationException("only message cache is implemented");
    }

    public Cache<String, List<WebChatMessage>> getMessageCache() {
        return getCacheContainer().getCache(MESSAGE_CACHE);
    }

    private EmbeddedCacheManager getCacheContainer() {
        if (manager == null) {
            GlobalConfiguration glob = new GlobalConfigurationBuilder()
                    .build();

            Configuration defaultConfig = new ConfigurationBuilder()
                    .clustering().cacheMode(CacheMode.LOCAL)
                    .build();
            manager = new DefaultCacheManager(glob, defaultConfig);
            manager.start();
        }
        return manager;
    }

    @PreDestroy
    public void cleanUp() {
        manager.stop();
        manager = null;
    }
}