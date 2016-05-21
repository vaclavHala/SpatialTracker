package cz.muni.fi.pv243.spatialtracker;

import static javax.ws.rs.core.HttpHeaders.WWW_AUTHENTICATE;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

    @Override
    public Response toResponse(final AuthenticationException exception) {
        return Response.status(UNAUTHORIZED)
                       .header(WWW_AUTHENTICATE, "Spatial Tracker")
                       .build();
    }
}
