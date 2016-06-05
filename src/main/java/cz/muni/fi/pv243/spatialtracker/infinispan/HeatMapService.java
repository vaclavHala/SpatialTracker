package cz.muni.fi.pv243.spatialtracker.infinispan;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.pv243.spatialtracker.issues.IssueResource;
import cz.muni.fi.pv243.spatialtracker.issues.dto.Coordinates;
import cz.muni.fi.pv243.spatialtracker.issues.dto.Heat;
import cz.muni.fi.pv243.spatialtracker.issues.filter.SpatialFilter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.round;
import static java.lang.Math.toIntExact;

@Stateless
public class HeatMapService {
    private double latitude;
    private double longitude;

    @Inject
    private CacheProvider cacheProvider;

    @Inject
    private IssueResource issueResource;

    @Inject
    private ObjectMapper json;

    public List<Heat> getHeatMapOfAllIssues(String rawFilter) throws IOException {
        List<Heat> result = new ArrayList<>();
        Map<String, Heat> help = new HashMap<>();

        SpatialFilter filter = json.readValue(rawFilter, SpatialFilter.class);
        if(filter == null) return null;

        latitude = (filter.latMax() - filter.latMin()) / 100;
        longitude = (filter.lonMin() - filter.lonMax()) / 100;

        cacheProvider.getIssueCache().values().stream().filter(issue ->
                issue.coords().latitude() <= filter.latMax() &&
                        issue.coords().latitude() >= filter.latMin() &&
                        issue.coords().longitude() <= filter.lonMax() &&
                        issue.coords().longitude() >= filter.lonMin())
                .forEach(issue -> {
                    int x = toIntExact(round((issue.coords().longitude() - filter.lonMax()) / longitude));
                    int y = toIntExact(round((filter.latMax() - issue.coords().latitude()) / latitude));
                    Heat h = help.get(x + ", " + y);
                    if (h == null) {
                        h = new Heat(
                                new Coordinates(filter.lonMax() + longitude * x, filter.latMax() - latitude * y),
                                new Coordinates(filter.lonMax() + longitude * x + 1, filter.latMax() - latitude * y + 1),
                                1);
                        help.put(x + ", " + y, h);
                    } else {
                        h.value(h.value() + 1);
                    }
                });
        help.forEach((k, v) -> result.add(v));
        return result;
    }
}
