package eu.hetes.gh_tracker.repository;

import eu.hetes.gh_tracker.TrackerException;
import eu.hetes.gh_tracker.model.EStatus;
import eu.hetes.gh_tracker.model.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;

abstract class BaseFileRepository implements IRepository {
    private static final Logger logger = LoggerFactory.getLogger(BaseFileRepository.class);

    private boolean connected = false;
    private final Object lock = new Object();
    private HashMap<Long, Issue> data = null;

    @Override
    public void connect() throws Exception {
        if (connected) return;
        synchronized (lock) {
            Issue[] issues = readFileContents();
            data = new HashMap<>();
            Arrays.stream(issues).forEach(issue -> data.put(issue.getId(), issue));
            connected = true;
        }
    }

    @Override
    public void disconnect() throws Exception {
        if (!connected) return;
        synchronized (lock) {
            data.clear();
            data = null;
            connected = false;
        }
    }

    @Override
    public Issue createIssue(Issue issue) throws Exception {
        // Check for connected status
        if (!connected) throw new Exception("Data file not opened");

        // Parameter check: issue not null, description specified, id must be 0
        if (issue == null || issue.getId() != 0 || issue.getDescription() == null) {
            throw new TrackerException("Invalid parameter(s) provided");
        }

        synchronized (lock) {
            // Get new ID and clone issue to add
            long newId = data.size() + 1; // data.size() + 1;
            Issue newItem = new Issue(newId, issue);
            data.put(newId, newItem);

            // Update file and return
            writeFileContents(data.values().toArray(Issue[]::new));
            return newItem.clone();
        }
    }

    @Override
    public Issue getIssue(long id) throws Exception {
        // Check for connected status
        if (!connected) throw new Exception("Data file not opened");

        synchronized (lock) {
            // Return cloned object or null if not found
            Issue found = data.get(id);
            return found != null ? found.clone() : null;
        }
    }

    @Override
    public Issue updateIssue(Issue issue) throws Exception {
        // Check for connected status
        if (!connected) throw new Exception("Data file not opened");

        // Parameter check: issue not null, description specified, id cannot be 0
        if (issue == null || issue.getId() == 0 || issue.getDescription() == null) {
            throw new TrackerException("Invalid parameter(s) provided");
        }

        synchronized (lock) {
            // Item should exist, otherwise nothing to update
            if (data.get(issue.getId()) == null) {
                return null;
            }

            // Update item
            Issue updatedItem = issue.clone();
            data.put(updatedItem.getId(), updatedItem);

            // Update file and return
            writeFileContents(data.values().toArray(Issue[]::new));
            return updatedItem.clone();
        }
    }

    @Override
    public Issue[] getIssuesByStatus(EStatus status) throws Exception {
        // Check for connected status
        if (!connected) throw new Exception("Data file not opened");

        synchronized (lock) {
            // If status not specified
            if (status == null) {
                return data.values().stream().map(Issue::clone).toArray(Issue[]::new);
            } else {
                return data.values().stream().filter(issue -> issue.getStatus() == status).map(Issue::clone).toArray(Issue[]::new);
            }
        }
    }

    protected abstract Issue[] readFileContents() throws Exception;

    protected abstract void writeFileContents(Issue[] issues) throws Exception;
}
