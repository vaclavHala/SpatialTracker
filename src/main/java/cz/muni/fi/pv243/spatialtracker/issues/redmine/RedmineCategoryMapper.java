
package cz.muni.fi.pv243.spatialtracker.issues.redmine;

import cz.muni.fi.pv243.spatialtracker.common.EnumMapper;
import static cz.muni.fi.pv243.spatialtracker.common.EnumMapper.Mapping.map;
import cz.muni.fi.pv243.spatialtracker.issues.IssueCategory;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.ADD;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.COMPLAINT;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.REMOVE;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.REPAIR;

public class RedmineCategoryMapper extends EnumMapper<IssueCategory>{

        private static final int REDMINE_ADD = 2;
    private static final int REDMINE_REMOVE = 3;
    private static final int REDMINE_REPAIR = 4;
    private static final int REDMINE_COMPLAINT = 5;

    public RedmineCategoryMapper() {
        super(map(REDMINE_ADD, ADD),
              map(REDMINE_REMOVE, REMOVE),
              map(REDMINE_REPAIR, REPAIR),
              map(REDMINE_COMPLAINT, COMPLAINT));
    }
}
