package cz.muni.fi.pv243.spatialtracker.users.redmine;

import cz.muni.fi.pv243.spatialtracker.*;
import static cz.muni.fi.pv243.spatialtracker.Closeable.closeable;
import cz.muni.fi.pv243.spatialtracker.config.Property;
import cz.muni.fi.pv243.spatialtracker.users.UserService;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_API_KEY;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.REDMINE_BASE_URL;
import static cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.assembleBasicAuthHeader;
import cz.muni.fi.pv243.spatialtracker.users.UserGroup;
import static cz.muni.fi.pv243.spatialtracker.users.UserGroup.USER;
import cz.muni.fi.pv243.spatialtracker.users.dto.*;
import cz.muni.fi.pv243.spatialtracker.users.dto.UserDetails.UserDetailsBuilder;
import cz.muni.fi.pv243.spatialtracker.users.redmine.dto.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.NotAuthorizedException;
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

    @Inject
    private RedmineGroupMapper groupMapper;

    @Override
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
                RedmineUserCreateResponse resp = redmineResponse.get().readEntity(RedmineUserCreateResponse.class);
                try {
                    this.addToGroup(resp.id(), this.groupMapper.toRedmineId(USER));
                } catch (MulticauseError e) {
                    log.warn("Could not add new user to group. Deleting from Redmine.");
                    this.deleteCurrentUser(newUser.login(), newUser.password());
                    throw e;
                }
                return resp.login();
            } else {
                List<String> errors = this.extractErrorReport(redmineResponse.get());
                log.info("Failed to create user: {}", errors);
                throw new MulticauseError(errors);
            }
        } catch (ProcessingException e) {
            throw new ServerError(e);
        }
    }

    @Override
    public void addToGroup(final String login, final UserGroup group) throws MulticauseError {
        RedmineUserDetails details = this.detailsRedmineSomeUser(login);
        this.addToGroup(details.id(), this.groupMapper.toRedmineId(group));
    }

    @Override
    public Optional<UserDetails> detailsSomeUser(final String login) {
        RedmineUserDetails redmineDetails = this.detailsRedmineSomeUser(login);
        if (redmineDetails == null) {
            return Optional.empty();
        }
        return Optional.of(this.mapRedmineUser(redmineDetails).build());
    }

    @Override
    public UserDetails detailsCurrentUser(final String login, final String password) throws MulticauseError {
        RedmineUserDetails redmineDetails = this.detailsRedmineCurrentUser(login, password);
        List<RedmineGroup> redmineGroups = this.groupMemberships(redmineDetails.id());
        List<UserGroup> groups = this.groupMapper.fromRedmine(redmineGroups);
        return this.mapRedmineUser(redmineDetails).memberships(groups).build();
    }

    @Override
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

    private void addToGroup(final long userId, final long groupId) throws MulticauseError {
        RedmineUserId redmineUserId = new RedmineUserId(userId);
        WebTarget target = this.restClient.target(this.redmineUrl +
                                                  format("groups/%d/users.json", groupId));
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header("X-Redmine-API-Key", this.redmineKey)
                                .buildPost(json(redmineUserId))
                                .invoke())) {
            if (redmineResponse.get().getStatus() == 200) {
                log.info("User #{} added to group #{}", userId, groupId);
            } else if (redmineResponse.get().getStatus() == 422) {
                List<String> errors = this.extractErrorReport(redmineResponse.get());
                log.warn("Failed to add user #{} to group #{}: {}",
                         userId, groupId, errors);
                throw new MulticauseError(errors);
            } else {
                log.info("Failed to add user #{} to group #{}: {} - {}",
                         userId, groupId,
                         redmineResponse.get().getStatus(),
                         redmineResponse.get().readEntity(String.class));
                throw new MulticauseError(asList("Could not add user to group."));
            }
        } catch (ProcessingException e) {
            throw new ServerError(e);
        }
    }

    private RedmineUserDetails detailsRedmineSomeUser(final String login) {
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
            return this.extractUser(login, redmineUsers.matchedUsers());
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
                throw new MulticauseError(asList("Unauthorized"));
            } else {
                List<String> errors = this.extractErrorReport(redmineResponse.get());
                log.info("Failed to display user: {}", errors);
                throw new MulticauseError(errors);
            }
        } catch (ProcessingException e) {
            throw new ServerError(e);
        }
    }

    private List<RedmineGroup> groupMemberships(final long userId) throws MulticauseError {
        //redmine only allows group view for administrators
        //we dont want all users to have admin access, so this is a workaround
        String groupsUrl = format("%susers/%d.json?include=groups",
                                  this.redmineUrl, userId);
        WebTarget target = this.restClient.target(groupsUrl);
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header("X-Redmine-API-Key", this.redmineKey)
                                .buildGet()
                                .invoke())) {
            if (redmineResponse.get().getStatus() == 200) {
                RedmineUserDetails redmineDetails = redmineResponse.get()
                                                                   .readEntity(RedmineUserDetailsCurrentWrapper.class)
                                                                   .user();
                log.debug("User #{} is in redmine groups {}", userId, redmineDetails.memberships());
                return redmineDetails.memberships() == null ? emptyList() : redmineDetails.memberships();
            } else {
                List<String> errors = this.extractErrorReport(redmineResponse.get());
                log.info("Failed obtain group memberships: {} - {}",
                         redmineResponse.get().getStatus(), errors);
                throw new MulticauseError(errors);
            }
        } catch (ProcessingException e) {
            throw new ServerError(e);
        }

    }

    private RedmineUserDetails extractUser(final String login, final List<RedmineUserDetails> foundUsers) {
        for (RedmineUserDetails match : foundUsers) {
            if (match.login().equals(login)) {
                return match;
            }
        }
        return null;
    }

    private UserDetailsBuilder mapRedmineUser(final RedmineUserDetails redmineUser) {
        return UserDetails.builder()
                          .login(redmineUser.login())
                          .firstname(redmineUser.firstname().equals(EMPTY) ? null : redmineUser.firstname())
                          .lastname(redmineUser.lastname().equals(EMPTY) ? null : redmineUser.lastname())
                          .email(redmineUser.email())
                          .icon(redmineUser.icon());
    }

    private List<String> extractErrorReport(final Response resp) {
        RedmineErrorReport redmineErrors = resp.readEntity(RedmineErrorReport.class);
        return redmineErrors == null ? emptyList() : redmineErrors.errors();
    }
}
