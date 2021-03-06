
package cz.muni.fi.pv243.spatialtracker.issues.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CategoryFilter.class, name = "category"),
    @JsonSubTypes.Type(value = SpatialFilter.class, name = "spatial"),
    @JsonSubTypes.Type(value = PriorityFilter.class, name = "priority"),
    @JsonSubTypes.Type(value = StatusFilter.class, name = "status"),
    @JsonSubTypes.Type(value = DateFilter.class, name = "date"),
})
public class IssueFilter {
}
