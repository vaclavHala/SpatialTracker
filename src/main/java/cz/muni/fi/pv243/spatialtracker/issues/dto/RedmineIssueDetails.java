package cz.muni.fi.pv243.spatialtracker.issues.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import cz.muni.fi.pv243.spatialtracker.redmine.dto.Coordinates;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@NoArgsConstructor(access = PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedmineIssueDetails {

    @Getter
    @JsonProperty("id")
    private long id;

    @Getter
    @JsonProperty("subject")
    private String subject;

    @Getter
    @JsonProperty("description")
    private String description;

    @JsonProperty("priority")
    private Priority priority;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("category")
    private Category category;

    @JsonProperty("author")
    private Author author;

    @Getter
    @JsonProperty
    @JsonUnwrapped
    private Coordinates coords;

    public String priority() {
        return this.priority.name;
    }

    public String status() {
        return this.status.name;
    }

    public String category() {
        return this.category.name;
    }

    public String author() {
        return this.author.name;
    }

    private static class Priority {

        String name;
    }

    private static class Status {

        String name;
    }

    private static class Category {

        String name;
    }

    private static class Author {

        String name;
    }
}
