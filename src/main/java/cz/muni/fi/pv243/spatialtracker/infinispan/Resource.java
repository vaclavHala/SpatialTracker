package cz.muni.fi.pv243.spatialtracker.infinispan;

import cz.muni.fi.pv243.spatialtracker.common.BackendServiceException;
import cz.muni.fi.pv243.spatialtracker.common.InvalidInputException;
import cz.muni.fi.pv243.spatialtracker.issues.IssueResource;
import cz.muni.fi.pv243.spatialtracker.issues.IssueService;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.issues.filter.CategoryFilter;
import cz.muni.fi.pv243.spatialtracker.issues.filter.IssueFilter;
import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import cz.muni.fi.pv243.spatialtracker.webchat.store.WebChatMessageStore;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.*;
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
    private IssueService issueService;

    @Inject
    private IssueResource issueResource;

    @Inject
    private HeatMapService heatMapService;

    @Inject
    private WebChatMessageStore webChatMessageStore;
    @POST
    @PermitAll
    @Path("/heat_map")
    public void createHeatMap(final @QueryParam("filter") String rawFilter) {
        try {
            List<IssueDetailsBrief> issues = issueService.searchFiltered(issueResource.getFilters(rawFilter));
        } catch (InvalidInputException e) {
            e.printStackTrace();
        } catch (BackendServiceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
//        heatMapService.addIssuesIntoCache();
        return heatMapService.getIssuesFromCache();
    }

    @GET
    @PermitAll
    @Path("/message")
    public List<WebChatMessage> createAndGetMessage(){
        webChatMessageStore.addMessage("prva", new WebChatMessage("meno", "testing", new Date()));
        return webChatMessageStore.getMessages("prva");
    }
}
