package cz.muni.fi.pv243.spatialtracker.issues.redmine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.muni.fi.pv243.spatialtracker.CustomField;
import cz.muni.fi.pv243.spatialtracker.RedmineLocalDateDeserializer;
import java.time.LocalDate;
import java.util.List;
import static lombok.AccessLevel.PRIVATE;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedmineIssueDetails {

    @JsonProperty("id")
    private long id;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("description")
    private String description;

    @JsonProperty("priority")
    private RedminePriority priority;

    @JsonProperty("status")
    private RedmineStatus status;

    @JsonProperty("category")
    private RedmineCategory category;

    @JsonProperty("author")
    private RedmineAuthor author;

    @JsonProperty("start_date")
    @JsonDeserialize(using = RedmineLocalDateDeserializer.class)
    private LocalDate startedDate;

    @JsonProperty("custom_fields")
    private List<CustomField> customFields;
}
