
package cz.muni.fi.pv243.spatialtracker.issues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.pv243.spatialtracker.issues.IssueEnums;
import lombok.Getter;

public class IssueCreate {

    @Getter
    @JsonProperty("subject")
    private String subject;

    @Getter
    @JsonProperty("description")
    private String description;

    @JsonProperty("priority")
    private IssueEnums.Priority priority;

//    @JsonProperty("status")
//    private IssueEnums.Status status;

    @JsonProperty("category")
    private IssueEnums.Category category;

//    @JsonProperty("author")
//    private IssueEnums.Author author;

}
