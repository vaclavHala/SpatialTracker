package cz.muni.fi.pv243.spatialtracker.issues.redmine.filter;

import cz.muni.fi.pv243.spatialtracker.issues.filter.SpatialFilter;
import static java.lang.String.format;

public class SpatialFilterMapper implements RedmineFilterMapper<SpatialFilter> {

    private static final int LATITUDE = 2;
    private static final int LONGITUDE = 3;

    @Override
    public Class<SpatialFilter> targetClass() {
        return SpatialFilter.class;
    }

    @Override
    public String filterFragmentFrom(final SpatialFilter declaration) {
        return format("cf_%d=><%f|%f&cf_%d=><%f|%f",
                      LATITUDE,
                      declaration.latMin(),
                      declaration.latMax(),
                      LONGITUDE,
                      declaration.lonMin(),
                      declaration.lonMax());

    }

}
