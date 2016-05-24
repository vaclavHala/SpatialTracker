
package cz.muni.fi.pv243.spatialtracker.config;

import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.INTEGRATION_PROJECT_ID;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.INTEGRATION_TRACKER_ID;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@ApplicationScoped
public class Config {

    @Produces
    @Property(REDMINE_API_KEY)
    private String redmineApiKey;

    @Produces
    @Property(REDMINE_BASE_URL)
    private String redmineBaseUrl;

    @Produces
    @Property(INTEGRATION_PROJECT_ID)
    private int projectId;

    @Produces
    @Property(INTEGRATION_TRACKER_ID)
    private int trackerId;

}
