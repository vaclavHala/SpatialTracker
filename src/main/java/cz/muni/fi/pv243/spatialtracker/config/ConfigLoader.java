package cz.muni.fi.pv243.spatialtracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Startup
@Singleton
public class ConfigLoader {

    @Inject
    private Config config;

    @Inject
    private ObjectMapper json;

    @PostConstruct
    public void load() {
        try (InputStream redmineFile = this.getClass().getResourceAsStream("/redmine.json")) {
            RedmineConfigJson redmine = this.json.readValue(redmineFile, RedmineConfigJson.class);
            this.config.redmineApiKey(redmine.apiKey());
            this.config.redmineBaseUrl(redmine.baseUrl());
            this.config.projectId(redmine.projectId());
            this.config.trackerId(redmine.trackerId());
            log.info("New Config: {}", this.config);
        } catch (IOException e) {
            log.error("Could not find config file for Redmine: /redmine.json");
            throw new IllegalStateException(e);
        }
    }
}
