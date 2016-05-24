package cz.muni.fi.pv243.spatialtracker.issues.redmine;

import cz.muni.fi.pv243.spatialtracker.EnumMapper;
import static cz.muni.fi.pv243.spatialtracker.EnumMapper.Mapping.map;
import cz.muni.fi.pv243.spatialtracker.issues.IssuePriority;
import static cz.muni.fi.pv243.spatialtracker.issues.IssuePriority.CAN_HAVE;
import static cz.muni.fi.pv243.spatialtracker.issues.IssuePriority.MUST_HAVE;
import static cz.muni.fi.pv243.spatialtracker.issues.IssuePriority.SHOULD_HAVE;
import static cz.muni.fi.pv243.spatialtracker.issues.IssuePriority.WANT_TO_HAVE;

public class RedminePriorityMapper extends EnumMapper<IssuePriority> {

    private static final int REDMINE_MUST_HAVE = 2;
    private static final int REDMINE_SHOULD_HAVE = 3;
    private static final int REDMINE_CAN_HAVE = 4;
    private static final int REDMINE_WANT_TO_HAVE = 5;

    public RedminePriorityMapper() {
        super(map(REDMINE_MUST_HAVE, MUST_HAVE),
              map(REDMINE_SHOULD_HAVE, SHOULD_HAVE),
              map(REDMINE_CAN_HAVE, CAN_HAVE),
              map(REDMINE_WANT_TO_HAVE, WANT_TO_HAVE));
    }
}
