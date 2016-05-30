package cz.muni.fi.pv243.spatialtracker.common;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class InvalidInputExceptionMapper implements ExceptionMapper<InvalidInputException> {

    @Override
    public Response toResponse(InvalidInputException e) {
        log.warn("Invalid input", e);
        Response.ResponseBuilder builder = Response.status(400);
        if (!e.errors().isEmpty()) {
            ErrorReport report = new ErrorReport(e.errors());
            builder.entity(report);
        }
        return builder.build();
    }
}
