package cz.muni.fi.pv243.spatialtracker.users;

import cz.muni.fi.pv243.spatialtracker.common.BackendServiceException;
import cz.muni.fi.pv243.spatialtracker.common.InvalidInputException;
import cz.muni.fi.pv243.spatialtracker.common.NotFoundException;
import cz.muni.fi.pv243.spatialtracker.common.UnauthorizedException;
import cz.muni.fi.pv243.spatialtracker.users.dto.*;

public interface UserService {

    String register(final UserCreate newUser)
            throws InvalidInputException, BackendServiceException;

    UserDetails detailsSomeUser(final String login)
            throws NotFoundException, BackendServiceException;

    UserDetails detailsCurrentUser(final String login, final String password)
            throws UnauthorizedException, BackendServiceException;

    //TODO do we update by field or as a whole?

    void deleteCurrentUser(final String login, final String password)
            throws UnauthorizedException, BackendServiceException;

}
