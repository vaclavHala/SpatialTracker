
package cz.muni.fi.pv243.spatialtracker.users.redmine;

import cz.muni.fi.pv243.spatialtracker.users.UserGroup;
import static cz.muni.fi.pv243.spatialtracker.users.UserGroup.USER;
import static cz.muni.fi.pv243.spatialtracker.users.UserGroup.WORKER;
import cz.muni.fi.pv243.spatialtracker.users.redmine.dto.RedmineGroup;
import static java.lang.String.format;
import java.util.List;
import static java.util.stream.Collectors.toList;

public class RedmineGroupMapper {

    private static final int REDMINE_LOGGED_IN = 6;
    private static final int REDMINE_WORKER = 7;

    public UserGroup fromRedmine(final RedmineGroup redmineGroup){
        switch(redmineGroup.id()){
            case REDMINE_LOGGED_IN: return USER;
            case REDMINE_WORKER: return WORKER;
        }
        throw new IllegalArgumentException(format("Unknown group: %d - %s",
                                                  redmineGroup.id(), redmineGroup.groupName()));
    }

    public List<UserGroup> fromRedmine(final List<RedmineGroup> redmineGroups) {
        return redmineGroups.stream().map(this::fromRedmine).collect(toList());
    }

    public long toRedmineId(final UserGroup group){
        switch(group){
            case USER: return REDMINE_LOGGED_IN;
            case WORKER: return REDMINE_WORKER;
        }
        throw new IllegalArgumentException("Unknown group: "+group);
    }

    public List<Long> toRedmineId(final List<UserGroup> groups) {
        return groups.stream().map(this::toRedmineId).collect(toList());
    }
}
