
package cz.muni.fi.pv243.spatialtracker.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import lombok.Setter;

@Setter
@ApplicationScoped
public class Config {

    @Produces
    @Property(REDMINE_API_KEY)
    private String redmineApiKey;

    @Produces
    @Property(REDMINE_BASE_URL)
    private String redmineBaseUrl;

}
