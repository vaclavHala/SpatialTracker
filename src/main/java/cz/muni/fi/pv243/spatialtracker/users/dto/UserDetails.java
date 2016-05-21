package cz.muni.fi.pv243.spatialtracker.users.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.pv243.spatialtracker.users.UserGroup;
import java.util.List;
import lombok.*;
import static lombok.AccessLevel.PRIVATE;

@ToString
@Getter
@Builder
@AllArgsConstructor(access =  PRIVATE)
@NoArgsConstructor(access =  PRIVATE)
@JsonInclude(NON_NULL)
public class UserDetails {

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

    @JsonProperty("memberships")
    private List<UserGroup> memberships;
}
