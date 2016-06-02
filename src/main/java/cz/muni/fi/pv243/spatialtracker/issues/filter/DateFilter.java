
package cz.muni.fi.pv243.spatialtracker.issues.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class DateFilter extends IssueFilter{

    @JsonProperty("from")
    private LocalDate from;

    @JsonProperty("to")
    private LocalDate to;
}
