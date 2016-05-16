
package cz.muni.fi.pv243.spatialtracker.redmine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Getter
@AllArgsConstructor
@JsonTypeName("issue")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
public class CreateIssue {

    @JsonProperty("project_id")
    private int project;
    @JsonProperty("tracker_id")
    private int tracker;
    @JsonProperty("category_id")
    private int category;
    @JsonProperty("priority_id")
    private int priority;
    @JsonProperty("status_id")
    private int status;
    @JsonProperty("subject")
    private String subject;

    @JsonUnwrapped
    @JsonProperty
    private Coordinates coords;


}
