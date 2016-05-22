
package cz.muni.fi.pv243.spatialtracker.issues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * For showing issues on the map.
 * Note that category, priority etc. are not included.
 * When user selects a filter only issues matching that
 * filter will be returned, therefore values such as category
 * are implicit, given by the filter.
 */
@ToString
@Getter
@AllArgsConstructor
public class IssueDetailsBrief {

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("coords")
    private Coordinates coords;

}
