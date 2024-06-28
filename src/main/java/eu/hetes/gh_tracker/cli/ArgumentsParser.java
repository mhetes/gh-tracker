package eu.hetes.gh_tracker.cli;

import eu.hetes.gh_tracker.model.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ArgumentsParser {
    private static final Logger logger = LoggerFactory.getLogger(ArgumentsParser.class);

    @Autowired
    private OutputFormatter formatter;

    /**
     * Parses Command line arguments
     * @param args Command Line arguments
     * @return Application parameters parsed from command line
     */
    public Parameters parse(String... args) {
        logger.debug("Parsing Arguments: {}", Arrays.toString(args));
        // If no arguments were specified
        if (args.length == 0) {
            logger.debug("No CMD arguments found");
            formatter.printHelp();
            return new Parameters(ECommand.None);
        }
        // Parse arguments or write error and help
        try {
            return parseImpl(args);
        } catch (Exception e) {
            logger.warn("Argument parsing error: {}: {}", e.getClass().getSimpleName(), e.getMessage());
            formatter.printError(e);
            formatter.printHelp();
            return new Parameters(ECommand.None);
        }
    }

    /**
     * Argument parser state machine implementation
     * @param args Command Line arguments
     * @return Application parameters parsed from command line
     * @throws Exception Parsing failed
     */
    private Parameters parseImpl(String... args) throws Exception {
        // States:
        // 0 - reading option
        // 1 - reading description
        // 2 - reading link or parentId
        // 3 - reading id required
        // 4 - reading id optional or end
        // 5 - end
        int state = 0;
        ECommand command = ECommand.None;
        String description = null;
        String link = null;
        long id = Constants.EMPTY_ID;

        for (String arg : args) {
            logger.debug("Parser step: arg={}, state={}", arg, state);
            switch (state) {
                case 0:
                    if (arg.compareTo("-c") == 0 || arg.compareTo("--create") == 0) {
                        logger.debug("Parsed result: command=Create, nextState=1");
                        command = ECommand.Create;
                        state = 1;
                        continue;
                    }
                    if (arg.compareTo("-x") == 0 || arg.compareTo("--close") == 0) {
                        logger.debug("Parsed result: command=Close, nextState=3");
                        command = ECommand.Close;
                        state = 3;
                        continue;
                    }
                    if (arg.compareTo("-l") == 0 || arg.compareTo("--list") == 0) {
                        logger.debug("Parsed result: command=List, nextState=5");
                        command = ECommand.List;
                        state = 5;
                        continue;
                    }
                    logger.debug("Invalid command option: {}", arg);
                    throw new Exception("Invalid option: " + arg);
                case 1:
                    logger.debug("Parsed result: description='{}', nextState=2", arg);
                    description = arg;
                    state = 2;
                    continue;
                case 2:
                    // Check if parameter can be parsed to number, if so, it is ID, otherwise it is link
                    try {
                        logger.debug("Trying to parse {} as parentId", arg);
                        id = Long.parseLong(arg);
                        logger.debug("Parsed result: parentId={}, nextState=5", id);
                        state = 5;
                    } catch (Exception e) {
                        logger.debug("Parsed result: link='{}', nextState=4", arg);
                        link = arg;
                        state = 4;
                    }
                    continue;
                case 3:
                case 4:
                    logger.debug("Trying to parse {} as issueId", arg);
                    id = Long.parseLong(arg);
                    logger.debug("Parsed result: issueId={}, nextState=5", id);
                    state = 5;
                    continue;
                case 5:
                    logger.debug("Received unexpected argument in final state: {}", arg);
                    throw new Exception("Unexpected parameter: " + arg);
            }
        }
        // Correct final state should be 4 or 5
        if (state < 4) {
            logger.debug("Argument parsing state machine not in final state: state={}", state);
            throw new Exception("Missing additional parameters");
        }
        // id must be positive
        if (id < 0) {
            logger.debug("Parsed issueId is not positive: issueId={}", id);
            throw new Exception("Invalid ID value specified");
        }
        return new Parameters(command, description, link, id);
    }
}
