package cz.muni.fi.pv243.spatialtracker.issues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.pv243.spatialtracker.issues.IssueCategory;
import cz.muni.fi.pv243.spatialtracker.issues.IssuePriority;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import static lombok.AccessLevel.PRIVATE;

@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class IssueCreate {

    @NotNull(message = "{issue.subject.empty}")
    @Size(min = 5, max = 50, message = "{issue.subject.size}")
    @JsonProperty("subject")
    private String subject;

    @Size(max = 1000, message = "{issue.description.size}")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "{issue.priority.empty}")
    @JsonProperty("priority")
    private IssuePriority priority;

    @NotNull(message = "{issue.category.empty}")
    @JsonProperty("category")
    private IssueCategory category;

    @NotNull(message = "{issue.coords.empty}")
    @JsonProperty("coords")
    private Coordinates coords;

}
