package eu.hetes.gh_tracker.cli;

import eu.hetes.gh_tracker.model.Constants;
import eu.hetes.gh_tracker.model.Issue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Component
public class OutputFormatter {

    @Value("${spring.application.name:tracker}")
    private String appName;

    @Value("${tracker.output.format:spreadsheet}")
    private String outputFormat;

    @Value("${tracker.output.date:yyyy-MM-dd'T'HH:mm}")
    private String dateFormat;

    @Value("${tracker.output.header.show:true}")
    private boolean showHeader;

    public void printError(Exception e) {
        String errorText = e.getClass().getSimpleName() + ": " + e.getMessage();
        System.err.println(errorText);
    }

    public void printHelp() {
        String asciiArt = """
                 _____         _   _            _ _   _       _____              _            \s
                |  __ \\       | | | |          | | | | |     |_   _|            | |           \s
                | |  \\/ ___   | |_| | ___  __ _| | |_| |__     | |_ __ __ _  ___| | _____ _ __\s
                | | __ / _ \\  |  _  |/ _ \\/ _` | | __| '_ \\    | | '__/ _` |/ __| |/ / _ \\ '__|
                | |_\\ \\ (_) | | | | |  __/ (_| | | |_| | | |   | | | | (_| | (__|   <  __/ |  \s
                 \\____/\\___/  \\_| |_/\\___|\\__,_|_|\\__|_| |_|   \\_/_|  \\__,_|\\___|_|\\_\\___|_|  \s
                                                                                              \s
                                                                                              \s
                """;
        String helpText =
                "usage: java -jar " + this.appName + ".jar" +
                System.lineSeparator() +
                " -c,--create <Description> [<Link> <ParentId>]     Create Issue" +
                System.lineSeparator() +
                " -l,--list                                         List open Issues" +
                System.lineSeparator() +
                " -x,--close <IssueId>                              Close Issue" +
                System.lineSeparator();
        System.out.print(asciiArt);
        System.out.print(helpText);
    }

    public void printIssue(Issue issue) {
        printIssues(new Issue[]{ issue });
    }

    public void printIssues(Issue[] issues) {
        String[] headers = {"ID", "Description", "ParentId", "Status", "CreationTimestamp", "Link"};
        String[][] data = convertData(issues);
        if (outputFormat.compareToIgnoreCase("tabs") == 0) {
            // Tabs format
            if (showHeader)
                System.out.print(getTabLine(headers));
            for (String[] line: data) {
                System.out.print(getTabLine(line));
            }
        } else {
            // Default Spreadsheet format
            int[] columnWidths = getColumnWidths(headers, data);
            System.out.print(getSpreadsheetBorder(columnWidths));
            if (showHeader) {
                System.out.print(getSpreadsheetLine(headers, columnWidths));
                System.out.print(getSpreadsheetBorder(columnWidths));
            }
            for (String[] line: data) {
                System.out.print(getSpreadsheetLine(line, columnWidths));
            }
            System.out.print(getSpreadsheetBorder(columnWidths));
        }
    }

    private String[][] convertData(Issue[] issues) {
        // Fields: ID, Description, ParentId, Status, CreationTimestamp, Link
        return Arrays.stream(issues).filter(Objects::nonNull).map(issue -> {
            return new String[]{
                    Long.toString(issue.getId()),
                    issue.getDescription() == null ? "" : issue.getDescription(),
                    issue.getParentId() == Constants.EMPTY_ID ? "" : Long.toString(issue.getParentId()),
                    issue.getStatus() == null ? "" : issue.getStatus().toString(),
                    new SimpleDateFormat(dateFormat).format(new Date(issue.getTimestamp())),
                    issue.getLink() == null ? "" : issue.getLink(),
            };
        }).toArray(String[][]::new);
    }

    private String getTabLine(String[] line) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length; i++) {
            sb.append(line[i]).append(i == line.length - 1 ? System.lineSeparator() : '\t');
        }
        return sb.toString();
    }

    private int[] getColumnWidths(String[] headers, String[][] data) {
        int len = headers.length;
        int[] res = new int[len];
        for (int i = 0; i < len; i++) {
            res[i] = getColumnWidth(headers, data, i);
        }
        return res;
    }

    private int getColumnWidth(String[] headers, String[][] data, int index) {
        return Integer.max(
                headers[index].length(),
                Arrays.stream(data).parallel().map(i -> i[index].length()).reduce(0, Integer::max)
        );
    }

    private String getSpreadsheetBorder(int[] columnWidths) {
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (int columnWidth : columnWidths) {
            sb.append("-".repeat(columnWidth + 2)).append('|');
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    private String getSpreadsheetLine(String[] line, int[] columnWidths) {
        int idx = 0;
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (String item : line) {
            sb.append(' ').append(item).append(" ".repeat(columnWidths[idx] - item.length() + 1)).append('|');
            idx++;
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }


}
