
package cz.muni.fi.pv243.spatialtracker.issues.redmine.filter;

import cz.muni.fi.pv243.spatialtracker.issues.filter.DateFilter;
import static java.lang.String.format;
import java.time.format.DateTimeFormatter;

public class DateFilterMapper implements RedmineFilterMapper<DateFilter> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public Class<DateFilter> targetClass() {
        return DateFilter.class;
    }

    @Override
    public String filterFragmentFrom(final DateFilter declaration) {
        String formattedFrom = FORMATTER.format(declaration.from());
        String formattedTo = FORMATTER.format(declaration.to());
        return format("created_on=><%s|%s",
                      formattedFrom,formattedTo);
    }
}
