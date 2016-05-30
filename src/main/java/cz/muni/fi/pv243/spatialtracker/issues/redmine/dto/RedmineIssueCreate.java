
package cz.muni.fi.pv243.spatialtracker.issues.redmine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonTypeName;
import cz.muni.fi.pv243.spatialtracker.common.redmine.CustomField;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@JsonTypeName("issue")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
public class RedmineIssueCreate {

        @JsonProperty("project_id")
    private long projectId;

    @JsonProperty("tracker_id")
    private long trackerId;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category_id")
    private long categoryId;

    @JsonProperty("priority_id")
    private long priorityId;

    @JsonProperty("custom_fields")
    private List<CustomField> custom;
}
//
//    {
//  "issue": {
//    "project_id": 4,
//    "tracker_id" : 1,
//    "subject": "Example",
//    "priority_id": 4,
//    "custom_fields":
//      [
//        {"value":"2","id":2},
//        {"value":"3","id":3}
//      ]
//  }
//}
//
//}
