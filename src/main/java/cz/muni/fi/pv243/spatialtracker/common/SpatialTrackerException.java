
package cz.muni.fi.pv243.spatialtracker.common;

public abstract class SpatialTrackerException extends Exception{

    public SpatialTrackerException() {
    }

    public SpatialTrackerException(String message) {
        super(message);
    }

    public SpatialTrackerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpatialTrackerException(Throwable cause) {
        super(cause);
    }
}
