package cz.muni.fi.pv243.spatialtracker.issues.redmine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class RedmineIssueCreateWrapper {

    //we cant use the @JsonTypeInfo trick with RedmineIssueCreate
    //because @JsonUnwrapped cant be used in conjunction with that (needed for custom_fields)
    @JsonProperty("issue")
    private RedmineIssueCreate wrapped;

}
