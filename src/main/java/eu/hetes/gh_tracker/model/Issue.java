package eu.hetes.gh_tracker.model;

import java.io.Serializable;

public class Issue extends BaseIssue implements Serializable, Cloneable {
    private final long id;
    private final long timestamp;
    private EStatus status;

    public Issue() {
        super();
        this.id = Constants.EMPTY_ID;
        this.timestamp = 0;
        this.status = null;
    }

    public Issue(String description, String link, long parentId) {
        super(description, link, parentId);
        this.id = Constants.EMPTY_ID;
        this.timestamp = 0;
        this.status = null;
    }

    public Issue(BaseIssue baseIssue) {
        super(baseIssue.getDescription(), baseIssue.getLink(), baseIssue.getParentId());
        this.id = Constants.EMPTY_ID;
        this.timestamp = 0;
        this.status = null;
    }

    public Issue(long id, long timestamp, EStatus status, String description, String link, long parentId) {
        super(description, link, parentId);
        this.id = id;
        this.timestamp = timestamp;
        this.status = status;
    }

    public Issue(long id, long timestamp, EStatus status, BaseIssue baseIssue) {
        super(baseIssue.getDescription(), baseIssue.getLink(), baseIssue.getParentId());
        this.id = id;
        this.timestamp = timestamp;
        this.status = status;
    }

    public Issue(long id, Issue issue) {
        super(issue.getDescription(), issue.getLink(), issue.getParentId());
        this.id = id;
        this.timestamp = issue.getTimestamp();
        this.status = issue.getStatus();
    }

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    @Override
    public Issue clone() {
        try {
            Issue clone = (Issue) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
