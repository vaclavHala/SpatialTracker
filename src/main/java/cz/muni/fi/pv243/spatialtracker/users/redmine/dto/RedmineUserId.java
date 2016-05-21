
package cz.muni.fi.pv243.spatialtracker.users.redmine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class RedmineUserId {

    @JsonProperty("user_id")
    private long id;
}
