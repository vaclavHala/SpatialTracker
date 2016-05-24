
package cz.muni.fi.pv243.spatialtracker.issues.redmine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import static lombok.AccessLevel.PRIVATE;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PRIVATE)
public class RedmineAuthor {

    @JsonProperty(value = "id")
    private int id;

    @JsonProperty(value = "name")
    private String fullName;
}
