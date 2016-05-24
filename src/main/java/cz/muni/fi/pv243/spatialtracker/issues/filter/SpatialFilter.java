package cz.muni.fi.pv243.spatialtracker.issues.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = PRIVATE)
public class SpatialFilter extends IssueFilter{

    @JsonProperty("lon_min")
    private double lonMin;

    @JsonProperty("lon_max")
    private double lonMax;

    @JsonProperty("lat_min")
    private double latMin;

    @JsonProperty("lat_max")
    private double latMax;
}
