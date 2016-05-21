package cz.muni.fi.pv243.spatialtracker.issues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import cz.muni.fi.pv243.spatialtracker.issues.IssueCategory;
import cz.muni.fi.pv243.spatialtracker.issues.IssuePriority;
import javax.validation.constraints.NotNull;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
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
