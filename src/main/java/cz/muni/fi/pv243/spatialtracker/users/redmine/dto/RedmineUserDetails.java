package cz.muni.fi.pv243.spatialtracker.users.redmine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import cz.muni.fi.pv243.spatialtracker.users.dto.RawIcon;
import java.util.List;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedmineUserDetails {

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
    @JsonUnwrapped
    private RawIcon icon;

    @JsonProperty("groups")
    private List<RedmineGroup> memberships;
}
