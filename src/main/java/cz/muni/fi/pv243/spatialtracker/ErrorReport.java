
package cz.muni.fi.pv243.spatialtracker;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class ErrorReport {

        @JsonProperty(value = "errors")
        private List<String> violations;
}
