
package cz.muni.fi.pv243.spatialtracker.users.redmine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = PRIVATE)
public class RedmineGroup {

    @JsonProperty(value = "id")
    private int id;

    @JsonProperty(value = "name")
    private String groupName;
}
