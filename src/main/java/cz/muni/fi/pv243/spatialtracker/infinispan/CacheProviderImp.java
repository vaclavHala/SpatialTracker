package cz.muni.fi.pv243.spatialtracker.infinispan;

import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CacheProviderImp implements CacheProvider {
    private static final String ISSUE_CACHE = "issues";
    private static final String MESSAGE_CACHE = "messages";
    private EmbeddedCacheManager manager;


    public Cache<String, IssueDetailsBrief> getIssueCache() {
        if (manager == null) initializeManager();
        return manager.getCache(ISSUE_CACHE);
    }

    public Cache<String, List<WebChatMessage>> getMessageCache() {
        if (manager == null) initializeManager();
        return manager.getCache(MESSAGE_CACHE);
    }

    private void initializeManager() {

        manager = new DefaultCacheManager();
        manager.start();

        Configuration issueConfig = new ConfigurationBuilder()
                .persistence()
                    .addSingleFileStore()
                    .location(System.getProperty("java.io.tmpdir"))
                    .preload(true)
                .invocationBatching().enable()
                .build();

        Configuration messageConfig = new ConfigurationBuilder()
                .persistence()
                    .addSingleFileStore()
                    .location(System.getProperty("java.io.tmpdir"))
                    .preload(true)
                .build();

        manager.defineConfiguration(ISSUE_CACHE, issueConfig);
        manager.defineConfiguration(MESSAGE_CACHE, messageConfig);
    }

    @PreDestroy
    public void cleanUp() {
        manager.stop();
        manager = null;
    }
}
