
package cz.muni.fi.pv243.spatialtracker.common.redmine;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CustomFieldsException extends Exception{

    private final List<String> missingFields;

    public CustomFieldsException(final List<String> missingFields) {
        super("Missing fields: "+missingFields);
        this.missingFields = new ArrayList<>(missingFields);
    }
}
