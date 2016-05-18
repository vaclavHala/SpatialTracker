package cz.muni.fi.pv243.spatialtracker.users;

import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    public static class LoginPass {

        private final String login;
        private final String pass;
    }
}
