package cz.muni.fi.pv243.spatialtracker.issues;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum IssueStatus {

    REPORTED,
    ACCEPTED,
    REJECTED,
    FIXED;

    static {
        REPORTED.allowedTransitions = EnumSet.of(ACCEPTED, REJECTED);
        ACCEPTED.allowedTransitions = EnumSet.of(REJECTED, FIXED);
        REJECTED.allowedTransitions = Collections.emptySet();
        FIXED.allowedTransitions = Collections.emptySet();
    }

    private Set<IssueStatus> allowedTransitions;

    public boolean canTransitionTo(final IssueStatus next) {
        return this.allowedTransitions.contains(next);
    }
}
