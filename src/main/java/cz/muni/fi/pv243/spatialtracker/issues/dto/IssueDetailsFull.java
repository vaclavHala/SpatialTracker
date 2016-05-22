
package cz.muni.fi.pv243.spatialtracker.issues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.muni.fi.pv243.spatialtracker.RedmineLocalDateDeserializer;
import cz.muni.fi.pv243.spatialtracker.RedmineLocalDateSerializer;
import cz.muni.fi.pv243.spatialtracker.issues.IssueCategory;
import cz.muni.fi.pv243.spatialtracker.issues.IssuePriority;
import cz.muni.fi.pv243.spatialtracker.issues.IssueStatus;
import java.time.LocalDate;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * For showing issue details when user views one concrete issue.
 */
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class IssueDetailsFull {

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private IssueStatus status;

    @JsonProperty("priority")
    private IssuePriority priority;

    @JsonProperty("category")
    private IssueCategory category;

    @JsonProperty("started")
    //TODO this should not be reused from redmine
    @JsonDeserialize(using = RedmineLocalDateDeserializer.class)
    @JsonSerialize(using = RedmineLocalDateSerializer.class)
    private LocalDate startedDate;

    @JsonProperty("author")
    private String authorLogin;

    @JsonProperty("coords")
    private Coordinates coords;

}
