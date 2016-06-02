
package cz.muni.fi.pv243.spatialtracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ObjectMapperProducer {

    @Produces
    private ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

}
