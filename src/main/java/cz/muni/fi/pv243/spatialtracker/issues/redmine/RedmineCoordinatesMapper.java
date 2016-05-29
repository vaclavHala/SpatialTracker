package cz.muni.fi.pv243.spatialtracker.issues.redmine;

import cz.muni.fi.pv243.spatialtracker.common.redmine.CustomField;
import cz.muni.fi.pv243.spatialtracker.common.redmine.CustomFieldsException;
import cz.muni.fi.pv243.spatialtracker.issues.dto.Coordinates;
import java.util.ArrayList;
import java.util.List;

public class RedmineCoordinatesMapper {

    static final int LATITUDE = 2;
    static final int LONGITUDE = 3;

    public Coordinates readFrom(final List<CustomField> customFields) throws CustomFieldsException{
        Double lon = null;
        Double lat = null;
        for (CustomField field : customFields) {
            switch (field.id()) {
                case LONGITUDE :
                    lon = Double.parseDouble(field.value());
                    break;
                case LATITUDE :
                    lat = Double.parseDouble(field.value());
                    break;
            }
            if (lon != null && lat != null) {
                return new Coordinates(lat, lon);
            }
        }
        List<String> missingFields = new ArrayList<>();
        if (lon == null) {
            missingFields.add("longitude");
        }
        if (lat == null) {
            missingFields.add("latitude");
        }
        throw new CustomFieldsException(missingFields);

    }

    public void appendTo(final List<CustomField> customFields, final Coordinates coords) {
        customFields.add(new CustomField(LATITUDE, String.valueOf(coords.latitude())));
        customFields.add(new CustomField(LONGITUDE, String.valueOf(coords.longitude())));
    }

}
