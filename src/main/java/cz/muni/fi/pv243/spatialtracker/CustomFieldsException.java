
package cz.muni.fi.pv243.spatialtracker;

import java.util.List;

public class CustomFieldsException extends RuntimeException{

    public CustomFieldsException(final List<String> missingFields) {
        super("Missing fields: "+missingFields);
    }
}
