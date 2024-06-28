package eu.hetes.gh_tracker;

import eu.hetes.gh_tracker.model.BaseIssue;
import eu.hetes.gh_tracker.model.Constants;
import eu.hetes.gh_tracker.model.EStatus;
import eu.hetes.gh_tracker.model.Issue;
import eu.hetes.gh_tracker.repository.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Tracker {

    @Autowired
    private IRepository repository;

    public Issue createIssue(BaseIssue issue) throws Exception {
        // User input check
        checkCreateParams(issue);
        // Create issue
        repository.connect();
        Issue created = repository.createIssue(new Issue(0, System.currentTimeMillis(), EStatus.Open, issue));
        repository.disconnect();
        return created;
    }

    public Issue closeIssue(long id) throws Exception {
        // User input check
        checkId(id, "Issue Id", true);
        // Find Issue by ID and close it
        repository.connect();
        Issue issue = repository.getIssue(id);
        if (issue == null) { // Issue not found
            throw new TrackerException("Cannot find issue with Id: " + id);
        }
        if (issue.getStatus() == EStatus.Closed) { // Issue already closed
            throw new TrackerException("Specified issue has been already closed");
        }
        issue.setStatus(EStatus.Closed);
        Issue updated = repository.updateIssue(issue);
        repository.disconnect();
        return updated;
    }

    public Issue[] listOpenIssues() throws Exception {
        repository.connect();
        Issue[] res = repository.getIssuesByStatus(EStatus.Open);
        repository.disconnect();
        return res;
    }

    private void checkCreateParams(BaseIssue issue) throws IllegalArgumentException {
        checkObject(issue, BaseIssue.class);
        String description = issue.getDescription();
        String link = issue.getLink();
        long parentId = issue.getParentId();
        checkString(description, "Issue Description", false, false);
        checkString(link, "Issue Link", true, true);
        checkId(parentId, "Parent Issue Id", true);
    }

    private void checkObject(Object obj, Class<?> clazz) throws IllegalArgumentException {
        if (obj == null) {
            throw new IllegalArgumentException(clazz.getSimpleName() + " cannot be null");
        }
    }

    private void checkString(String str, String name, boolean allowNull, boolean allowEmpty) throws IllegalArgumentException {
        if (str == null) {
            if (allowNull) return;
            throw new IllegalArgumentException(name + " cannot be null");
        }
        if (!allowEmpty && str.trim().compareTo("") == 0) {
            throw new IllegalArgumentException(name + " cannot be empty");
        }
    }

    private void checkId(long num, String name, boolean allowEmptyValue) throws IllegalArgumentException {
        if (num < 0) {
            throw new IllegalArgumentException(name + " cannot be negative number");
        }
        if (!allowEmptyValue && num == Constants.EMPTY_ID) {
            throw new IllegalArgumentException(name + " must be specified");
        }
    }
}
