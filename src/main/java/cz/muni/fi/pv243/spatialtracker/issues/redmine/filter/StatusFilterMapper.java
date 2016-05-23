
package cz.muni.fi.pv243.spatialtracker.issues.redmine.filter;

import cz.muni.fi.pv243.spatialtracker.issues.filter.StatusFilter;
import cz.muni.fi.pv243.spatialtracker.issues.redmine.RedmineStatusMapper;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import javax.inject.Inject;

public class StatusFilterMapper implements RedmineFilterMapper<StatusFilter> {
    @Inject
    private RedmineStatusMapper mapper;

    @Override
    public Class<StatusFilter> targetClass() {
        return StatusFilter.class;
    }

    @Override
    public String filterFragmentFrom(final StatusFilter declaration) {
        String formattedCats = declaration.interestStates().stream()
                                          .map(cat -> String.valueOf(this.mapper.toId(cat)))
                                          .collect(joining("|"));
        return format("status_id=" + formattedCats);
    }
}
