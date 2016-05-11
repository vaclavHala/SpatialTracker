
package cz.muni.fi.pv243.spatialtracker;

import static java.util.stream.Collectors.toList;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JsonErrorMapper implements ExceptionMapper<ConstraintViolationException>{

    @Override
    public Response toResponse(final ConstraintViolationException exception) {
        exception.getConstraintViolations().forEach(System.out::println);
        ErrorReport report =
                new ErrorReport(exception.getConstraintViolations().stream()
                .map((ConstraintViolation<?> v) -> v.getMessage())
                .collect(toList()));
        return Response.status(400)
                .entity(report)
                .build();
    }

}
