
package cz.muni.fi.pv243.spatialtracker.redmine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NONE;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@JsonTypeInfo(use = NONE)
public class CustomFields {

    @JsonProperty("custom_fields")
    private List<CustomField> fields;

    @AllArgsConstructor
    public static class CustomField{

        @JsonProperty("id")
        private int fieldIf;
        @JsonProperty("value")
        private String value;

    }
}
