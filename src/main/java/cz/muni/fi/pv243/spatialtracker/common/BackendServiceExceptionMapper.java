package cz.muni.fi.pv243.spatialtracker.common;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class BackendServiceExceptionMapper implements ExceptionMapper<BackendServiceException> {

    @Override
    public Response toResponse(BackendServiceException e) {
        log.warn("Internal error", e);
        return Response.status(500).build();
    }
}
