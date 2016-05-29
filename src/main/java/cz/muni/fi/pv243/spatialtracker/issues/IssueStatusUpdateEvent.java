
package cz.muni.fi.pv243.spatialtracker.issues;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class IssueStatusUpdateEvent {

    private final long issueId;
    private final IssueStatus previousStatus;
    private final IssueStatus currentStatus;
}
