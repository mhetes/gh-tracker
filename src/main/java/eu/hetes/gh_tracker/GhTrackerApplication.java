package eu.hetes.gh_tracker;

import eu.hetes.gh_tracker.cli.ArgumentsParser;
import eu.hetes.gh_tracker.cli.ECommand;
import eu.hetes.gh_tracker.cli.OutputFormatter;
import eu.hetes.gh_tracker.cli.Parameters;
import eu.hetes.gh_tracker.model.BaseIssue;
import eu.hetes.gh_tracker.model.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GhTrackerApplication implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(GhTrackerApplication.class);

	@Autowired
	private OutputFormatter formatter;

	@Autowired
	private ArgumentsParser argParser;

	@Autowired
	private Tracker tracker;

	@Override
	public void run(String... args) {
		Parameters params = argParser.parse(args);
		try {
			switch (params.getCommand()) {
				case ECommand.Create -> {
					Issue issue = tracker.createIssue(new BaseIssue(params.getDescription(), params.getLink(), params.getId()));
					formatter.printIssue(issue);
				}
				case ECommand.Close -> {
					Issue issue = tracker.closeIssue(params.getId());
					formatter.printIssue(issue);
				}
				case ECommand.List -> {
					Issue[] issues = tracker.listOpenIssues();
					formatter.printIssues(issues);
				}
			}
		} catch (Exception e) {
			logger.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
			formatter.printError(e);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(GhTrackerApplication.class, args);
	}
}
