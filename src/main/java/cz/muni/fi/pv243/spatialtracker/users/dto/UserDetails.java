package cz.muni.fi.pv243.spatialtracker.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
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
}
