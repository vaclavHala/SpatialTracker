package cz.muni.fi.pv243.spatialtracker.issues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.pv243.spatialtracker.issues.IssueCategory;
import cz.muni.fi.pv243.spatialtracker.issues.IssuePriority;
import javax.validation.constraints.NotNull;
import lombok.*;
import static lombok.AccessLevel.PRIVATE;

@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class IssueCreate {

    @NotNull
    @JsonProperty("subject")
    private String subject;

    @JsonProperty("description")
    private String description;

    @NotNull
    @JsonProperty("priority")
    private IssuePriority priority;

    @NotNull
    @JsonProperty("category")
    private IssueCategory category;

    @JsonProperty("coords")
    private Coordinates coords;

}
