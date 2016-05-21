package cz.muni.fi.pv243.spatialtracker.users;

import cz.muni.fi.pv243.spatialtracker.MulticauseError;
import cz.muni.fi.pv243.spatialtracker.users.dto.*;
import java.util.Optional;

public interface UserService {

    String register(final UserCreate newUser) throws MulticauseError;

    void addToGroup(String login, UserGroup group) throws MulticauseError ;

    Optional<UserDetails> detailsSomeUser(final String login);

    //TODO need to come up with how / if we need this
    //List<UserDetails> detailsFilteredUsers(final Predicate<UserDetails> query);

    UserDetails detailsCurrentUser(final String login, final String password) throws MulticauseError;

    //TODO do we update by field or as a whole?

    void deleteCurrentUser(final String login, final String password) throws MulticauseError;

}
