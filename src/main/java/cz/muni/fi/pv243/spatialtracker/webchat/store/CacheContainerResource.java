package cz.muni.fi.pv243.spatialtracker.webchat.store;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.TransactionMode;

@ApplicationScoped
public class CacheContainerResource implements CacheContainerProvider {

    private BasicCacheContainer manager;

    public BasicCacheContainer getCacheContainer() {
        if (manager == null) {
            GlobalConfiguration glob = new GlobalConfigurationBuilder()
                    .nonClusteredDefault().globalJmxStatistics().enable()
                    .jmxDomain("org.infinispan.carmart")  // prevent collision with non-transactional carmart
                    .build();

            Configuration defaultConfig = new ConfigurationBuilder()
                    .transaction().transactionMode(TransactionMode.TRANSACTIONAL)
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