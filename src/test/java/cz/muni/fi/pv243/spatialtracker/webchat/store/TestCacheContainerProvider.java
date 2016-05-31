package cz.muni.fi.pv243.spatialtracker.webchat.store;

import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import static javax.interceptor.Interceptor.Priority.APPLICATION;

@Priority(APPLICATION)
@ApplicationScoped
public class TestCacheContainerProvider implements CacheContainerProvider {

    private BasicCacheContainer manager;

    public BasicCacheContainer getCacheContainer() {
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