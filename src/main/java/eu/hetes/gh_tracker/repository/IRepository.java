package eu.hetes.gh_tracker.repository;

import eu.hetes.gh_tracker.model.EStatus;
import eu.hetes.gh_tracker.model.Issue;

public interface IRepository {
    void connect() throws Exception;
    void disconnect() throws Exception;
    Issue createIssue(Issue issue) throws Exception;
    Issue getIssue(long id) throws Exception;
    Issue updateIssue(Issue issue) throws Exception;
    Issue[] getIssuesByStatus(EStatus status) throws Exception;
}
