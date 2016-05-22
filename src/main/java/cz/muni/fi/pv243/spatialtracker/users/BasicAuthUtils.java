package cz.muni.fi.pv243.spatialtracker.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Base64;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BasicAuthUtils {

    public static LoginPass decodeBasicAuthLogin(final String authHeaderValue) {
        try {
            String loginPassAuthPart = authHeaderValue.split(" ")[1];
            String[] loginPass = new String(Base64.getDecoder().decode(loginPassAuthPart)).split(":");
            return new LoginPass(loginPass[0], loginPass[1]);
        } catch (IndexOutOfBoundsException | IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    public static String assembleBasicAuthHeader(final String login, final String password) {
        String encodedLoginPass = Base64.getEncoder().encodeToString((login + ':' + password).getBytes());
        return "Basic " + encodedLoginPass;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PRIVATE)
    public static class LoginPass {

        @JsonProperty("login")
        private String login;
        
        @JsonProperty("pass")
        private String pass;
    }
}
