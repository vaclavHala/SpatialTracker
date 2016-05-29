
package cz.muni.fi.pv243.spatialtracker.common.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = PRIVATE)
public class RedmineErrorReport {

        @JsonProperty(value = "errors")
        private List<String> errors;
}
