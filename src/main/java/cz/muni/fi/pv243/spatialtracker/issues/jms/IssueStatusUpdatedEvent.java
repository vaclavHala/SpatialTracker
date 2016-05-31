package cz.muni.fi.pv243.spatialtracker.issues.jms;

import cz.muni.fi.pv243.spatialtracker.issues.IssueStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IssueStatusUpdatedEvent implements Serializable {
    private long ID;
    private IssueStatus status;
}
