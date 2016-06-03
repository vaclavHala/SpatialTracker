package cz.muni.fi.pv243.spatialtracker.infinispan;

import cz.muni.fi.pv243.spatialtracker.common.BackendServiceException;
import cz.muni.fi.pv243.spatialtracker.common.InvalidInputException;
import cz.muni.fi.pv243.spatialtracker.issues.IssueService;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.issues.filter.CategoryFilter;
import cz.muni.fi.pv243.spatialtracker.issues.filter.IssueFilter;
import org.infinispan.Cache;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;

import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.*;

/**
 * @author opontes
 */
@ApplicationScoped
public class InitialBatch {
    @Inject
    private CacheProvider cacheProvider;

    @Inject
    private IssueService issueService;

    private Cache<String, IssueDetailsBrief> cache;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        System.out.println("------------INIT---------------");
        cache = cacheProvider.getIssueCache();
        try {
            issueService.searchFiltered(new ArrayList<IssueFilter>() {
                {
                    add(new CategoryFilter(ADD, REMOVE, REPAIR, COMPLAINT));
                }
            }).forEach(issue -> cache.put(Long.toString(issue.id()), issue));

        } catch (InvalidInputException | BackendServiceException e) {
            e.printStackTrace();
        }
    }

    public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init) {
        System.out.println("-----------Destroy-------------");
    }
}
