package cz.muni.fi.pv243.spatialtracker.common;

import lombok.Getter;
import lombok.ToString;

/**
 * Thrown when the resource user wishes to access does not exist.
 */
@Getter
@ToString
public class NotFoundException extends SpatialTrackerException {

    private final Object searchedForIdentifier;

    public NotFoundException(Object searchedForIdentifier) {
        super();
        this.searchedForIdentifier = searchedForIdentifier;
    }
}
