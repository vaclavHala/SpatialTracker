
package cz.muni.fi.pv243.spatialtracker.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;

@ApplicationScoped
public class Config {

    @Produces
    @Property(REDMINE_API_KEY)
    private String redmineApiKey = "264acfed33b8af628991dda4de64d75390854d82";

    @Produces
    @Property(REDMINE_BASE_URL)
    private String redmineBaseUrl = "http://localhost:3000/";

}
