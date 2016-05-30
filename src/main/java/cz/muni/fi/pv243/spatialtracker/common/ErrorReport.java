
package cz.muni.fi.pv243.spatialtracker.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class ErrorReport {

        @JsonProperty(value = "errors")
        private List<String> errors;
}
