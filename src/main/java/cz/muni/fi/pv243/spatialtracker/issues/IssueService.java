
package cz.muni.fi.pv243.spatialtracker.issues;

import cz.muni.fi.pv243.spatialtracker.MulticauseError;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsFull;
import java.util.Optional;

public interface IssueService {

    long report(IssueCreate newIssue, String login, String password) throws MulticauseError;

    Optional<IssueDetailsFull> detailsFor(long issueId);



}
