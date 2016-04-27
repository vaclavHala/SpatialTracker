package cz.muni.fi.pv243.spatialtracker.redmine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.NotNull;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
@JsonTypeName("project")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
public class CreateProject {

        @NotNull
        @JsonProperty(value = "identifier")
        private String identifier;

        @NotNull
        @JsonProperty(value = "name")
        private String name;
}
