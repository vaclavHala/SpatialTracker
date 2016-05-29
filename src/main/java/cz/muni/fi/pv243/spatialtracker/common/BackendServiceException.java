
package cz.muni.fi.pv243.spatialtracker.common;

import lombok.ToString;

/**
 * Thrown when one of depended-on services is unavailable,
 * returns unexpected values or reports its own internal error.
 */
@ToString
public class BackendServiceException extends SpatialTrackerException{

    public BackendServiceException(String message) {
        super(message);
    }

    public BackendServiceException(Throwable cause) {
        super(cause);
    }
}
