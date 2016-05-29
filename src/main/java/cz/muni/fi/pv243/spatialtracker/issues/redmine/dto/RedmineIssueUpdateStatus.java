
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
public class RedmineIssueUpdateStatus {

    @JsonProperty("status_id")
    private int status;

}
