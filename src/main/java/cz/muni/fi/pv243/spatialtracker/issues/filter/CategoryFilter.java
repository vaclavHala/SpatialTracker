
package cz.muni.fi.pv243.spatialtracker.issues.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.pv243.spatialtracker.issues.IssueCategory;
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
public class CategoryFilter extends IssueFilter{

    @JsonProperty("in")
    private Set<IssueCategory> interestCategories;

    public CategoryFilter(final IssueCategory... cats){
        this.interestCategories = new HashSet<>(asList(cats));
    }
}
