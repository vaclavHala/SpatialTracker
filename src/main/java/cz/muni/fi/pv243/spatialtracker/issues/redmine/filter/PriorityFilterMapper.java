package cz.muni.fi.pv243.spatialtracker.issues.redmine.filter;

import cz.muni.fi.pv243.spatialtracker.issues.IssuePriority;
import cz.muni.fi.pv243.spatialtracker.issues.filter.PriorityFilter;
import cz.muni.fi.pv243.spatialtracker.issues.redmine.RedminePriorityMapper;
import static java.lang.String.format;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import static java.util.stream.Collectors.joining;
import javax.inject.Inject;

public class PriorityFilterMapper implements RedmineFilterMapper<PriorityFilter> {

    @Inject
    RedminePriorityMapper mapper;

    @Override
    public Class<PriorityFilter> targetClass() {
        return PriorityFilter.class;
    }

    @Override
    public String filterFragmentFrom(final PriorityFilter declaration) {
        List<IssuePriority> priorityRange =
                asList(IssuePriority.values()).subList(declaration.minPriority().ordinal(),
                                                       declaration.maxPriority().ordinal() + 1);

        return format("priority_id=%s",
                      priorityRange.stream()
                                   .map(p -> String.valueOf(this.mapper.toId(p)))
                                   .collect(joining("|")));
    }
}
