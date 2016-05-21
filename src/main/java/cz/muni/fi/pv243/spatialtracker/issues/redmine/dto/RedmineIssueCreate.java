
package cz.muni.fi.pv243.spatialtracker.issues.redmine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import cz.muni.fi.pv243.spatialtracker.CustomField;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class RedmineIssueCreate {

        @JsonProperty("project_id")
    private long projectId;

    @JsonProperty("tracker_id")
    private long trackerId;

    @JsonProperty("subject")
    private String subject;

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
