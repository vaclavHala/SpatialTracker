package cz.muni.fi.pv243.spatialtracker.users.redmine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonTypeName;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = PRIVATE)
@JsonTypeName("user")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedmineUserCreateResponse {

    @JsonProperty("id")
    private long id;

    @JsonProperty("login")
    private String login;

    @JsonProperty("api_key")
    private String apiKey;
}
