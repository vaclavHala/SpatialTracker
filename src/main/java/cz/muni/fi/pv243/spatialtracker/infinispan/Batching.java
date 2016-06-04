package cz.muni.fi.pv243.spatialtracker.infinispan;

import cz.muni.fi.pv243.spatialtracker.common.BackendServiceException;
import cz.muni.fi.pv243.spatialtracker.common.InvalidInputException;
import cz.muni.fi.pv243.spatialtracker.issues.IssueService;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.issues.filter.CategoryFilter;
import cz.muni.fi.pv243.spatialtracker.issues.filter.DateFilter;
import cz.muni.fi.pv243.spatialtracker.issues.filter.IssueFilter;
import org.apache.deltaspike.scheduler.api.Scheduled;
import org.infinispan.Cache;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.*;

/**
 * @author opontes
 */
@Scheduled(cronExpression = "0 0 2 * * ?")
class Batching implements Job{

    @Inject
    private CacheProvider cacheProvider;

    @Inject
    private IssueService issueService;

    private Cache<String, IssueDetailsBrief> cache;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        cache = cacheProvider.getIssueCache();
        if (cache.values().isEmpty()) {
            addIssuesIntoCache(new ArrayList<IssueFilter>() {{
                add(new CategoryFilter(ADD, REMOVE, REPAIR, COMPLAINT));
            }});
        } else {
            addIssuesIntoCache(new ArrayList<IssueFilter>() {{
                add(new DateFilter(LocalDate.now().minusDays(1), LocalDate.now()));
            }});
        }
    }

    private void addIssuesIntoCache(List<IssueFilter> filters) {
        try {
            cache.startBatch();
            issueService.searchFiltered(filters)
                    .forEach(issue -> cache.put(Long.toString(issue.id()), issue));
            cache.endBatch(true);
        } catch (InvalidInputException | BackendServiceException e) {
            e.printStackTrace();
            cache.endBatch(false);
        }
    }
}
