package cz.muni.fi.pv243.spatialtracker.infinispan;

import cz.muni.fi.pv243.spatialtracker.issues.IssueResource;
import cz.muni.fi.pv243.spatialtracker.issues.dto.Heat;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
@Stateless
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/cache")
@DeclareRoles("USER")
@RolesAllowed("USER")
public class Resource {
    @Inject
    private IssueResource issueResource;

    @Inject
    private HeatMapService heatMapService;

    @POST
    @PermitAll
    @Path("/heat_map")
    public List<Heat> createHeatMap(final @QueryParam("filter") String rawFilter) {
        try {
            return heatMapService.getHeatMapOfAllIssues(issueResource.getFilters(rawFilter));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
