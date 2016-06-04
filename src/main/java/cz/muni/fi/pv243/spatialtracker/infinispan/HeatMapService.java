package cz.muni.fi.pv243.spatialtracker.infinispan;

import cz.muni.fi.pv243.spatialtracker.issues.IssueService;
import cz.muni.fi.pv243.spatialtracker.issues.dto.Coordinates;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import org.infinispan.Cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class HeatMapService {
    private Cache<String, IssueDetailsBrief> cache;

    @Inject
    private IssueService issueService;

    @Inject
    private CacheProvider cacheProvider;

    private void initializeCache(){
        cache = cacheProvider.getIssueCache();
    }

    public void addIssuesIntoCache(){
        if(cache == null) initializeCache();
        List<IssueDetailsBrief> issues = new ArrayList<>();
        for(int i = 0; i < 1000; i++){
            issues.add(new IssueDetailsBrief(i, "TestIssue" + i, new Coordinates((long) 49.2000+i*10, (long) 16.5940+i*10)));
        }
        issues.forEach(issue -> cache.put(Long.toString(issue.id()), issue));
    }

    public List<IssueDetailsBrief> getIssuesFromCache(){
        if(cache == null) initializeCache();
        List<IssueDetailsBrief> result = new ArrayList<>();
        cache.values().stream().forEach(x -> result.add(x));
        return result;
    }
}
