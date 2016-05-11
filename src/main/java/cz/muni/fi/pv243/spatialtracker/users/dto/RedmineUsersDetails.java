package cz.muni.fi.pv243.spatialtracker.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedmineUsersDetails {

    @JsonProperty("users")
    private List<UserMatch> matchedUsers;

    @Getter
    @NoArgsConstructor(access = PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserMatch {

        @JsonProperty("id")
        private long id;
        @JsonProperty("login")
        private String login;
        @JsonProperty("firstname")
        private String firstname;
        @JsonProperty("lastname")
        private String lastname;
        @JsonProperty("mail")
        private String email;
        @JsonProperty
        private RawIcon icon;
    }
}
