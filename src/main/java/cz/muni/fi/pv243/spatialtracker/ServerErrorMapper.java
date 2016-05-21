
package cz.muni.fi.pv243.spatialtracker;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class ServerErrorMapper implements ExceptionMapper<ServerError> {

    @Override
    public Response toResponse(final ServerError exception) {
        log.error("Uncaught exception:",exception);
        return Response.serverError().build();
    }
}
