package cz.muni.fi.pv243.spatialtracker.users.redmine;

import cz.muni.fi.pv243.spatialtracker.common.Closeable;
import cz.muni.fi.pv243.spatialtracker.common.redmine.RedmineErrorReport;
import cz.muni.fi.pv243.spatialtracker.*;
import cz.muni.fi.pv243.spatialtracker.common.*;
import static cz.muni.fi.pv243.spatialtracker.common.Closeable.closeable;
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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public String register(final UserCreate newUser) throws InvalidInputException, BackendServiceException {
        RedmineUserCreate redmineNewUser = new RedmineUserCreate(newUser.login(),
                                                                 newUser.password(),
                                                                 EMPTY,
                                                                 EMPTY,
                                                                 newUser.email());
        RedmineUserCreateResponse newRedmineUser;
        WebTarget target = this.restClient.target(this.redmineUrl + "users.json");
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header("X-Redmine-API-Key", this.redmineKey)
                                .buildPost(json(redmineNewUser))
                                .invoke())) {
            if (redmineResponse.get().getStatus() == 201) {
                log.info("User was created in Redmine");
                newRedmineUser = redmineResponse.get().readEntity(RedmineUserCreateResponse.class);
            } else if (redmineResponse.get().getStatus() == 422) {
                List<String> errors = this.extractErrorReport(redmineResponse.get());
                throw new InvalidInputException(errors);
            } else {
                throw new BackendServiceException(redmineResponse.get().readEntity(String.class));
            }
        } catch (ProcessingException e) {
            throw new BackendServiceException(e);
        }

        try {
            this.addToGroup(newRedmineUser.id(), this.groupMapper.toRedmineId(USER));
        } catch (NotFoundException | UnauthorizedException e) {
            throw new BackendServiceException(e);
        }

        return newRedmineUser.login();
    }

    @Override
    public UserDetails detailsSomeUser(final String login) throws NotFoundException, BackendServiceException {
        return this.mapRedmineUser(this.detailsRedmineSomeUser(login));
    }

    @Override
    public UserDetails detailsCurrentUser(final String login, final String password) throws UnauthorizedException, BackendServiceException {
        //this blows up with invalid credentials
        RedmineUserDetails redmineDetails = this.detailsRedmineCurrentUser(login, password);
        //redmine only allows group view for administrators
        //we dont want all users to have admin access, so this is a workaround
        try {
            RedmineUserDetails redmineDetailsWithGroups = this.detailsRedmineSomeUser(redmineDetails.id());
            return this.mapRedmineUser(redmineDetailsWithGroups);
        } catch (NotFoundException e) {
            //we have just checked if the user exists above and he did
            //something fishy here, probably a race, definitely our fault though
            throw new BackendServiceException(e);
        }
    }

    @Override
    public void deleteCurrentUser(final String login, final String password) throws UnauthorizedException, BackendServiceException {
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
            if (redmineResponse.get().getStatus() == 200) {
                log.debug("User {} was deleted in Redmine", login);
            } else if (redmineResponse.get().getStatus() == 403) {

            } else if (redmineResponse.get().getStatus() == 0) {

            } else {
                throw new BackendServiceException(redmineResponse.get().readEntity(String.class));
            }
        } catch (ProcessingException e) {
            throw new BackendServiceException(e);
        }
    }

    public void addToGroup(final long userId, final long groupId) throws NotFoundException,
                                                                 UnauthorizedException,
                                                                 BackendServiceException {
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
            } else if (redmineResponse.get().getStatus() == 404) {
                throw new NotFoundException(userId);
            } else {
                throw new BackendServiceException(redmineResponse.get().readEntity(String.class));
            }
        } catch (ProcessingException e) {
            throw new BackendServiceException(e);
        }
    }

    public RedmineUserDetails detailsRedmineSomeUser(final String login) throws NotFoundException, BackendServiceException {
        WebTarget target = this.restClient.target(this.redmineUrl + "users.json")
                                          .queryParam("name", login)
                                          .queryParam("limit", 1);
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header("X-Redmine-API-Key", this.redmineKey)
                                .buildGet()
                                .invoke())) {
            if (redmineResponse.get().getStatus() == 200) {
                RedmineUserDetailsSearchWrapper redmineUsers =
                        redmineResponse.get().readEntity(RedmineUserDetailsSearchWrapper.class);
                RedmineUserDetails redmineDetails = this.extractUser(login, redmineUsers.matchedUsers());
                if (redmineDetails == null) {
                    throw new NotFoundException(login);
                }
                return redmineDetails;
            } else {
                throw new BackendServiceException(redmineResponse.get().readEntity(String.class));
            }
        } catch (ProcessingException e) {
            throw new BackendServiceException(e);
        }
    }

    public RedmineUserDetails detailsRedmineSomeUser(final long redmineUserId) throws NotFoundException, BackendServiceException {
        WebTarget target = this.restClient.target(this.redmineUrl).path("users").path(redmineUserId + ".json")
                                          .queryParam("include", "groups");
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header("X-Redmine-API-Key", this.redmineKey)
                                .buildGet()
                                .invoke())) {
            if (redmineResponse.get().getStatus() == 200) {
                return redmineResponse.get()
                                      .readEntity(RedmineUserDetailsCurrentWrapper.class)
                                      .user();
            } else if (redmineResponse.get().getStatus() == 404) {
                throw new NotFoundException(redmineUserId);
            } else {
                throw new BackendServiceException(redmineResponse.get().readEntity(String.class));
            }
        } catch (ProcessingException e) {
            throw new BackendServiceException(e);
        }
    }

    public RedmineUserDetails detailsRedmineCurrentUser(
            final String login,
            final String password) throws UnauthorizedException, BackendServiceException {
        WebTarget target = this.restClient.target(this.redmineUrl).path("users/current.json");
        try (Closeable<Response> redmineResponse =
                closeable(target.request(APPLICATION_JSON)
                                .header(AUTHORIZATION, assembleBasicAuthHeader(login, password))
                                .buildGet()
                                .invoke())) {
            if (redmineResponse.get().getStatus() == 200) {
                return redmineResponse.get().readEntity(RedmineUserDetailsCurrentWrapper.class).user();
            } else if (redmineResponse.get().getStatus() == 401) {
                throw new UnauthorizedException(login);
            } else {
                throw new BackendServiceException(redmineResponse.get().readEntity(String.class));
            }
        } catch (ProcessingException e) {
            throw new BackendServiceException(e);
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

    private UserDetails mapRedmineUser(final RedmineUserDetails redmineUser) {
        return UserDetails.builder()
                          .login(redmineUser.login())
                          .firstname(redmineUser.firstname().equals(EMPTY) ? null : redmineUser.firstname())
                          .lastname(redmineUser.lastname().equals(EMPTY) ? null : redmineUser.lastname())
                          .memberships(redmineUser.memberships() == null ? null : this.groupMapper.fromRedmine(redmineUser.memberships()))
                          .email(redmineUser.email())
                          .icon(redmineUser.icon())
                          .build();
    }

    private List<String> extractErrorReport(final Response resp) {
        RedmineErrorReport redmineErrors = resp.readEntity(RedmineErrorReport.class);
        return redmineErrors == null ? emptyList() : redmineErrors.errors();
    }
}
