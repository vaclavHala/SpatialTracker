package cz.muni.fi.pv243.spatialtracker.issues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class Coordinates implements Serializable {

    @NotNull(message = "{coords.lat.empty}")
    @JsonProperty("lat")
    private double latitude;

    @NotNull(message = "{coords.lon.empty}")
    @JsonProperty("lon")
    private double longitude;
}
