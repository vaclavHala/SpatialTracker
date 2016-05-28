package cz.muni.fi.pv243.spatialtracker.infinispan;

import cz.muni.fi.pv243.spatialtracker.issues.IssueCategory;
import cz.muni.fi.pv243.spatialtracker.issues.IssueService;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.issues.filter.CategoryFilter;
import cz.muni.fi.pv243.spatialtracker.issues.filter.IssueFilter;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author opontes
 */
public class HeatMapService {
    private final EmbeddedCacheManager cacheManager;
    private Cache<String, IssueDetailsBrief> cache;

    @Inject
    IssueService issueService;

    public HeatMapService() throws IOException {
        cacheManager = new DefaultCacheManager("infinispan.xml");
        cache = cacheManager.getCache();
    }

    public void addIssuesIntoCache(){
        issueService.searchFiltered(new ArrayList<IssueFilter>(){{ add(new CategoryFilter(IssueCategory.ADD)); }})
                .forEach(issue -> cache.put(Long.toString(issue.id()), issue));
    }

    public List<IssueDetailsBrief> getIssuesFromCache(){
        List<IssueDetailsBrief> result = new ArrayList<>();
        cache.values().stream().forEach(x -> result.add(x));
        return result;
    }
}
