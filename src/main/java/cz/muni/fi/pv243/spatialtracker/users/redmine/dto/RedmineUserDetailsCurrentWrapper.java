
package cz.muni.fi.pv243.spatialtracker.users.redmine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Getter
@NoArgsConstructor(access = PRIVATE)
public class RedmineUserDetailsCurrentWrapper {

    @JsonProperty("user")
    private RedmineUserDetails user;

}
