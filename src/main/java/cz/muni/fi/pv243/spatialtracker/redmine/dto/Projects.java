package cz.muni.fi.pv243.spatialtracker.redmine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import lombok.ToString;

@ToString
//@JsonTypeName("projects")
//@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Projects {

    @JsonProperty
    private List<Project> projects;
    @JsonProperty("total_count")
    private int totalCount;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Project {

        @JsonProperty
        private long id;
        @JsonProperty
        private String name;
        @JsonProperty
        private String identifier;
        @JsonProperty
        private int status;
    }
}
