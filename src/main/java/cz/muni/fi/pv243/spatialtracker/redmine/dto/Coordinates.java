package cz.muni.fi.pv243.spatialtracker.redmine.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
@JsonSerialize(using = CoordinatesSerializer.class)
@JsonDeserialize(using = CoordinatesDeserializer.class)
public class Coordinates {

    private double latitude;
    private double longitude;
}
