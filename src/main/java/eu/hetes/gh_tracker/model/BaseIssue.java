package eu.hetes.gh_tracker.model;

public class BaseIssue {
    private String description;
    private String link;
    private long parentId;

    public BaseIssue() {
        this.description = null;
        this.link = null;
        this.parentId = Constants.EMPTY_ID;
    }

    public BaseIssue(String description, String link, long parentId) {
        this.description = description;
        this.link = link;
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
}
