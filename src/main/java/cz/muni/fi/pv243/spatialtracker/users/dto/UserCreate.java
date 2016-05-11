
package cz.muni.fi.pv243.spatialtracker.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class UserCreate {

    @NotNull(message = "{user.login.empty}")
    @JsonProperty("login")
    private String login;

    @NotNull(message = "{user.password.empty}")
    @Size(min = 3, message = "{user.password.weak}")
    @JsonProperty("password")
    private String password;


    @NotNull(message = "{user.email.empty}")
    //this pattern is just first line of defense,
    //real validation will be performed by Redmine
    @Pattern(regexp = ".+@.+\\..+", message = "{user.email.invalid}")
    @JsonProperty("mail")
    private String email;

}
