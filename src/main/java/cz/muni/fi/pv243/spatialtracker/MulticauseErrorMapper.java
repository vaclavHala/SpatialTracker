package cz.muni.fi.pv243.spatialtracker;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MulticauseErrorMapper implements ExceptionMapper<MulticauseError> {

    @Override
    public Response toResponse(final MulticauseError exception) {
        Response.ResponseBuilder builder = Response.status(400);
        if (!exception.errors().isEmpty()) {
            ErrorReport report = new ErrorReport(exception.errors());
            builder.entity(report);
        }
        return builder.build();
    }
}
