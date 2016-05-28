
package cz.muni.fi.pv243.spatialtracker.issues;

import cz.muni.fi.pv243.spatialtracker.MulticauseError;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsFull;
import cz.muni.fi.pv243.spatialtracker.issues.filter.IssueFilter;
import java.util.List;
import java.util.Optional;

public interface IssueService {

    long report(IssueCreate newIssue, String login) throws MulticauseError;

    Optional<IssueDetailsFull> detailsFor(long issueId);

    List<IssueDetailsBrief> searchFiltered(List<IssueFilter> filters);

    void updateIssueState(long issueId, IssueStatus newStatus);


}
