package cz.muni.fi.pv243.spatialtracker.webchat.store;

import org.infinispan.commons.api.BasicCacheContainer;

public interface CacheContainerProvider {
    BasicCacheContainer getCacheContainer();
}