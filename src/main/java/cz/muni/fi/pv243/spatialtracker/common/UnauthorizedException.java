
package cz.muni.fi.pv243.spatialtracker.common;

import lombok.ToString;

/**
 * Thrown when a user tries to perform an action
 * without being authorized to.
 */
@ToString
public class UnauthorizedException extends SpatialTrackerException{

    public UnauthorizedException(String message) {
        super(message);
    }
}
