package cz.muni.fi.pv243.spatialtracker.infinispan;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * @author opontes
 */
@ApplicationScoped
public class InitialBatch {
    @Inject
    Batching batching;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        System.out.println("------------INIT---------------");
        batching.execute();
    }

    public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init) {
        System.out.println("-----------Destroy-------------");
    }
}
