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
    @Inject
    private CacheProvider cacheProvider;

    @Inject
    private ObjectMapper json;

    public List<Heat> getHeatMapOfAllIssues(String rawFilter) throws IOException {
        List<Heat> result = new ArrayList<>();
        Map<String, Heat> help = new HashMap<>();

        SpatialFilter filter = json.readValue(rawFilter, SpatialFilter.class);
        
        if(filter == null) {
            return null;
        }

        cacheProvider.getIssueCache().values().stream().filter(issue ->
                issue.coords().latitude() <= filter.latMax() &&
                        issue.coords().latitude() >= filter.latMin() &&
                        issue.coords().longitude() <= filter.lonMax() &&
                        issue.coords().longitude() >= filter.lonMin())
                .forEach(issue -> {
                    int x = toIntExact(round(issue.coords().longitude() * 100));
                    int y = toIntExact(round(issue.coords().latitude() * 100));
                    
                    Heat h = help.get(x + ", " + y);
                    
                    if (h == null) {
                        h = new Heat(new Coordinates(y / 100d, x / 100d), new Coordinates((y + 1) / 100d, (x + 1) / 100d), 1);
                        
                        help.put(x + ", " + y, h);
                    } else {
                        h.value(h.value() + 1);
                    }
                });
        
        help.forEach((k, v) -> result.add(v));
        
        return result;
    }
}
