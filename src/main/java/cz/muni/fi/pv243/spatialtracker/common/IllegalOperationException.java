
package cz.muni.fi.pv243.spatialtracker.common;

import lombok.ToString;

/**
 * Thrown when the target of a operation exists
 * and caller is authorized to perform operation of this type,
 * however the context, parameterization etc. of the particular
 * invocation are invalid.
 */
@ToString
public class IllegalOperationException extends SpatialTrackerException{

    public IllegalOperationException(String message) {
        super(message);
    }

    public IllegalOperationException(Throwable cause) {
        super(cause);
    }
}
