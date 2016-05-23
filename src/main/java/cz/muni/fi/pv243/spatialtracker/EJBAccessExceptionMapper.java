package cz.muni.fi.pv243.spatialtracker;

import javax.ejb.EJBAccessException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EJBAccessExceptionMapper implements ExceptionMapper<EJBAccessException> {

    @Override
    public Response toResponse(final EJBAccessException exception) {
        return Response.status(Response.Status.FORBIDDEN).build();
    }
}
