package cz.muni.fi.pv243.spatialtracker.users.redmine;

import cz.muni.fi.pv243.spatialtracker.*;
import static cz.muni.fi.pv243.spatialtracker.Closeable.closeable;
import cz.muni.fi.pv243.spatialtracker.config.Property;
import cz.muni.fi.pv243.spatialtracker.users.UserService;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import static cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.assembleBasicAuthHeader;
import cz.muni.fi.pv243.spatialtracker.users.dto.*;
import cz.muni.fi.pv243.spatialtracker.users.redmine.dto.RedmineUserCreate;
import cz.muni.fi.pv243.spatialtracker.users.redmine.dto.RedmineUserDetails;
import cz.muni.fi.pv243.spatialtracker.users.redmine.dto.RedmineUserDetailsCurrentWrapper;
import cz.muni.fi.pv243.spatialtracker.users.redmine.dto.RedmineUserDetailsSearchWrapper;
import java.io.IOException;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import static javax.ws.rs.client.Entity.json;
import javax.ws.rs.client.WebTarget;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named("RedmineUserService")
public class RedmineUserService implements UserService {

    private static final String EMPTY = "-";

    @Inject
    @Property(REDMINE_API_KEY)
    private String redmineKey;
    @Inject
    @Property(REDMINE_BASE_URL)
    private String redmineUrl;

    @Inject
    private Client restClient;

    public String register(final UserCreate newUser) throws MulticauseError {
        RedmineUserCreate redmineNewUser = new RedmineUserCreate(newUser.login(),
                                                                 newUser.password(),
                                                                 EMPTY,
                                                                 EMPTY,
                                                                 newUser.email());
        WebTarget target = this.restClient.target(this.redmineUrl + "users.json");
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header("X-Redmine-API-Key", this.redmineKey)
                                .buildPost(json(redmineNewUser))
                                .invoke())) {
            if (redmineResponse.get().getStatus() == 201) {
                log.info("User was created in Redmine");
                return newUser.login();
            } else {
                List<String> errors = this.extractErrorReport(redmineResponse.get());
                log.info("Failed to create user: {}", errors);
                throw new MulticauseError(errors);
            }
        } catch (ProcessingException e) {
            throw new ServerError(e);
        }
    }

    public Optional<UserDetails> detailsSomeUser(final String login) {
        WebTarget target = this.restClient.target(this.redmineUrl + "users.json")
                                          .queryParam("name", login)
                                          .queryParam("limit", 1);
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header("X-Redmine-API-Key", this.redmineKey)
                                .buildGet()
                                .invoke())) {
            RedmineUserDetailsSearchWrapper redmineUsers =
                    redmineResponse.get().readEntity(RedmineUserDetailsSearchWrapper.class);
            return Optional.ofNullable(this.extractUser(login, redmineUsers.matchedUsers()));
        } catch (ProcessingException e) {
            throw new ServerError(e);
        }
    }

    public UserDetails detailsCurrentUser(final String login, final String password) throws MulticauseError {
        return this.mapRedmineUser(this.detailsRedmineCurrentUser(login, password));
    }

    public void deleteCurrentUser(final String login, final String password) throws MulticauseError {
        log.info("Request to delete current user: {}", login);
        RedmineUserDetails redmineUser = this.detailsRedmineCurrentUser(login, password);
        String deleteUserUri = format("%susers/%d.json",
                                      this.redmineUrl,
                                      redmineUser.id());
        WebTarget target = this.restClient.target(deleteUserUri);
        try (Closeable<Response> redmineResponse =
                closeable(target.request()
                                .header("X-Redmine-API-Key", this.redmineKey)
                                .buildDelete()
                                .invoke())) {
            if (redmineResponse.get().getStatus() != 200) {
                List<String> errors = this.extractErrorReport(redmineResponse.get());
                log.info("Failed to delete user: {}", errors);
                throw new MulticauseError(errors);
            }
        } catch (ProcessingException e) {
            throw new ServerError(e);
        }
    }

    private RedmineUserDetails detailsRedmineCurrentUser(
            final String login,
            final String password) throws MulticauseError {
        WebTarget target = this.restClient.target(this.redmineUrl + "users/current.json");
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header(AUTHORIZATION, assembleBasicAuthHeader(login, password))
                                .buildGet()
                                .invoke())) {
            if (redmineResponse.get().getStatus() == 200) {
                return redmineResponse.get().readEntity(RedmineUserDetailsCurrentWrapper.class).user();
            } else if (redmineResponse.get().getStatus() == 401) {
                    throw new AuthenticationException();
            } else {
                List<String> errors = this.extractErrorReport(redmineResponse.get());
                log.info("Failed to display user: {}", errors);
                throw new MulticauseError(errors);
            }
        } catch (ProcessingException e) {
            throw new ServerError(e);
        }
    }

    private UserDetails extractUser(final String login, final List<RedmineUserDetails> foundUsers) {
        for (RedmineUserDetails match : foundUsers) {
            if (match.login().equals(login)) {
                return this.mapRedmineUser(match);
            }
        }
        return null;
    }

    private UserDetails mapRedmineUser(final RedmineUserDetails redmineUser) {
        return new UserDetails(redmineUser.login(),
                               redmineUser.firstname().equals(EMPTY) ? null : redmineUser.firstname(),
                               redmineUser.lastname().equals(EMPTY) ? null : redmineUser.lastname(),
                               redmineUser.email(),
                               redmineUser.icon());
    }

    private List<String> extractErrorReport(final Response resp) {
        RedmineErrorReport redmineErrors = resp.readEntity(RedmineErrorReport.class);
        return redmineErrors == null ? emptyList() : redmineErrors.errors();
    }
}
