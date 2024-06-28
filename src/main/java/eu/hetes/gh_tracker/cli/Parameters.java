package eu.hetes.gh_tracker.cli;

import eu.hetes.gh_tracker.model.Constants;

public class Parameters {
    private ECommand command;
    private long id;
    private String description;
    private String link;

    public Parameters(ECommand command) {
        this.command = command;
        this.id = Constants.EMPTY_ID;
        this.description = null;
        this.link = null;
    }

    public Parameters(ECommand command, String description, String link, long id) {
        this.command = command;
        this.id = id;
        this.description = description;
        this.link = link;
    }

    public ECommand getCommand() {
        return command;
    }

    public void setCommand(ECommand command) {
        this.command = command;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
