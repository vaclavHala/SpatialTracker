package cz.muni.fi.pv243.spatialtracker.common;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class IllegalOperationExceptionMapper implements ExceptionMapper<IllegalOperationException> {

    @Override
    public Response toResponse(IllegalOperationException e) {
        log.warn("Illegal operation", e);
        return Response.status(403).build();
    }
}
