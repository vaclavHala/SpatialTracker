package cz.muni.fi.pv243.spatialtracker.issues.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.pv243.spatialtracker.issues.IssueStatus;
import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class StatusFilter extends IssueFilter {

    @JsonProperty("in")
    private Set<IssueStatus> interestStates;

    public StatusFilter(final IssueStatus... stats) {
        this.interestStates = new HashSet<>(asList(stats));
    }
}
