
package cz.muni.fi.pv243.spatialtracker.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = PRIVATE)
public class RedmineUserDetailsCurrentWrapper {

    @JsonProperty("user")
    private RedmineUserDetails user;

}
