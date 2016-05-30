
package cz.muni.fi.pv243.spatialtracker.config;

import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.INTEGRATION_PROJECT_ID;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.INTEGRATION_TRACKER_ID;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_HOST;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_PORT;
import static java.lang.String.format;
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
    @Property(REDMINE_HOST)
    private String redmineHost;

    @Produces
    @Property(REDMINE_PORT)
    private int redminePort;

    @Produces
    @Property(REDMINE_BASE_URL)
    private String redmineBaseUrl(){
        return format("%s:%d/", this.redmineHost, this.redminePort);
    }

    @Produces
    @Property(INTEGRATION_PROJECT_ID)
    private int projectId;

    @Produces
    @Property(INTEGRATION_TRACKER_ID)
    private int trackerId;

}
