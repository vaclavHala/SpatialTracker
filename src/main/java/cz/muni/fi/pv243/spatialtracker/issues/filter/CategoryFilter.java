
package cz.muni.fi.pv243.spatialtracker.issues.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.pv243.spatialtracker.issues.IssueCategory;
import java.util.Set;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = PRIVATE)
public class CategoryFilter extends IssueFilter{

    @JsonProperty("in")
    private Set<IssueCategory> interestCategories;

}
