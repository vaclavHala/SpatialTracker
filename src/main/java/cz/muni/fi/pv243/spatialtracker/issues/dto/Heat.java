package cz.muni.fi.pv243.spatialtracker.issues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author opontes
 */
@ToString
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class Heat {
    @JsonProperty("coordinates1")
    private Coordinates c1;
    @JsonProperty("coordinates2")
    private Coordinates c2;
    @JsonProperty("value")
    private Integer value;
}
