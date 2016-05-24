
package cz.muni.fi.pv243.spatialtracker.issues.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.pv243.spatialtracker.issues.IssuePriority;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = PRIVATE)
public class PriorityFilter  extends IssueFilter{

    @JsonProperty("min")
    private IssuePriority minPriority;
    @JsonProperty("max")
    private IssuePriority maxPriority;

}
