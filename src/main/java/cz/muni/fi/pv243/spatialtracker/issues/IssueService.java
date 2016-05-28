package cz.muni.fi.pv243.spatialtracker.issues;

import cz.muni.fi.pv243.spatialtracker.common.*;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueCreate;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsFull;
import cz.muni.fi.pv243.spatialtracker.issues.filter.IssueFilter;
import java.util.List;

public interface IssueService {

    /**
     *
     * @param newIssue
     * @param login
     * @return
     * @throws UnauthorizedException
     * @throws BackendServiceException
     */
    long report(IssueCreate newIssue, String login)
            throws UnauthorizedException, InvalidInputException, BackendServiceException;

    /**
     *
     * @param issueId
     * @param newStatus
     * @throws UnauthorizedException
     * @throws IllegalOperationException
     * @throws BackendServiceException
     */
    void updateIssueState(long issueId, IssueStatus newStatus)
            throws UnauthorizedException, NotFoundException, IllegalOperationException, BackendServiceException;

    /**
     *
     * @param issueId
     * @return
     * @throws NotFoundException
     * @throws BackendServiceException
     */
    IssueDetailsFull detailsFor(long issueId)
            throws NotFoundException, BackendServiceException;

    /**
     *
     * @param filters
     * @return
     * @throws BackendServiceException
     */
    List<IssueDetailsBrief> searchFiltered(List<IssueFilter> filters)
            throws InvalidInputException, BackendServiceException;
}
