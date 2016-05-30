package cz.muni.fi.pv243.spatialtracker.webchat.store;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;

@ApplicationScoped
public class CacheContainerResource implements CacheContainerProvider {
    public static final String ISSUE_CACHE = "issues";

    private BasicCacheContainer manager;

    public BasicCacheContainer getCacheContainer() {
        if (manager == null) {
            GlobalConfiguration glob = new GlobalConfigurationBuilder()
                    .nonClusteredDefault().globalJmxStatistics().enable()
                    .jmxDomain("org.infinispan.carmart")  // prevent collision with non-transactional carmart
                    .build();

            Configuration defaultConfig = new ConfigurationBuilder()
                    .transaction().transactionMode(TransactionMode.TRANSACTIONAL)
                    .invocationBatching().enable()
                    .build();




            manager = new DefaultCacheManager(glob, defaultConfig);
            manager.start();
        }
        return manager;
    }

    public Cache<String, IssueDetailsBrief> getIssueCache(){
        Configuration issueCacheConfig = new ConfigurationBuilder()
                .jmxStatistics().enable()
                .clustering().cacheMode(CacheMode.LOCAL)
                .transaction().transactionMode(TransactionMode.TRANSACTIONAL).autoCommit(false)
                .lockingMode(LockingMode.OPTIMISTIC).transactionManagerLookup(new GenericTransactionManagerLookup())
                .locking().isolationLevel(IsolationLevel.REPEATABLE_READ)
                .eviction().maxEntries(4).strategy(EvictionStrategy.LRU)
                .persistence().passivation(true).addSingleFileStore().purgeOnStartup(true)
                .build();

        ((DefaultCacheManager) manager).defineConfiguration(ISSUE_CACHE, issueCacheConfig);
        return null;
    }

    @PreDestroy
    public void cleanUp() {
        manager.stop();
        manager = null;
    }
}