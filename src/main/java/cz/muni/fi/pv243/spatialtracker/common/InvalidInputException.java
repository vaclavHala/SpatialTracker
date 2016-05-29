
package cz.muni.fi.pv243.spatialtracker.common;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

/**
 * Thrown when input received from user is incomplete, contains invalid values etc.
 * This exception contains collection of all errors found in the users input .
 */
@Getter
@ToString
public class InvalidInputException extends SpatialTrackerException{

    private final List<String> errors;

    public InvalidInputException(final List<String> errors) {
        super(errors.toString());
        this.errors = new ArrayList<>(errors);
    }
}
