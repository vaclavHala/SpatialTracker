
package cz.muni.fi.pv243.spatialtracker.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class RedmineConfigJson {

    @JsonProperty("api_key")
    private String apiKey;

    @JsonProperty("host")
    private String host;

    @JsonProperty("port")
    private int port;

    @JsonProperty("integration_project_id")
    private int projectId;

    @JsonProperty("integration_tracker_id")
    private int trackerId;
}
