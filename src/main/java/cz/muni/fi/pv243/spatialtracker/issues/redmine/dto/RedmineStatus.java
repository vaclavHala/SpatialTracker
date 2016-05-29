
package cz.muni.fi.pv243.spatialtracker.issues.redmine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import static lombok.AccessLevel.PRIVATE;

@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class RedmineStatus {

    @JsonProperty(value = "id")
    private int id;

    @JsonProperty(value = "name")
    private String statusName;
}
