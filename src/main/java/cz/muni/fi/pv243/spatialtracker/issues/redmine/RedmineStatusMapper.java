
package cz.muni.fi.pv243.spatialtracker.issues.redmine;

import cz.muni.fi.pv243.spatialtracker.common.EnumMapper;
import static cz.muni.fi.pv243.spatialtracker.common.EnumMapper.Mapping.map;
import cz.muni.fi.pv243.spatialtracker.issues.IssueStatus;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueStatus.ACCEPTED;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueStatus.FIXED;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueStatus.REJECTED;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueStatus.REPORTED;

public class RedmineStatusMapper extends EnumMapper<IssueStatus>{

    private static final int REDMINE_REPORTED = 2;
     private static final int REDMINE_ACCEPTED = 3;
    private static final int REDMINE_REJECTED = 4;
    private static final int REDMINE_FIXED = 5;

    public RedmineStatusMapper() {
        super(map(REDMINE_REPORTED, REPORTED),
              map(REDMINE_ACCEPTED, ACCEPTED),
              map(REDMINE_REJECTED, REJECTED),
              map(REDMINE_FIXED, FIXED));
    }
}
