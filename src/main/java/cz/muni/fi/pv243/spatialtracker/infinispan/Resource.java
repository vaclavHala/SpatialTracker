package cz.muni.fi.pv243.spatialtracker.infinispan;

import cz.muni.fi.pv243.spatialtracker.MulticauseError;
import cz.muni.fi.pv243.spatialtracker.issues.IssueCategory;
import cz.muni.fi.pv243.spatialtracker.issues.IssueResource;
import cz.muni.fi.pv243.spatialtracker.issues.IssueService;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.issues.filter.CategoryFilter;
import cz.muni.fi.pv243.spatialtracker.issues.filter.IssueFilter;
import cz.muni.fi.pv243.spatialtracker.issues.filter.PriorityFilter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.ADD;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.COMPLAINT;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.REPAIR;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author opontes
 */
@Slf4j
@Stateless
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/cache")
@DeclareRoles("USER")
@RolesAllowed("USER")
public class Resource {
    @Inject
    IssueService issueService;

    @Inject
    IssueResource issueResource;

    @Inject
    HeatMapService heatMapService;

    @POST
    @PermitAll
    @Path("/heat_map")
    public void createHeatMap(final @QueryParam("filter") String rawFilter) throws MulticauseError {
        List<IssueDetailsBrief> issues = issueService.searchFiltered(issueResource.getFilters(rawFilter));

    }

    @GET
    @PermitAll
    @Path("/test")
    public List<IssueFilter> test(){
        List<IssueFilter> filters = new ArrayList<>();
        filters.add(new CategoryFilter(REPAIR));
        filters.add(new CategoryFilter(ADD, COMPLAINT, REPAIR));
        filters.add(new CategoryFilter(ADD, COMPLAINT));
        return filters;
    }

    @GET
    @PermitAll
    @Path("/orig")
    public List<IssueDetailsBrief> getFromCache(){
        heatMapService.addIssuesIntoCache();
        return heatMapService.getIssuesFromCache();
    }
}
