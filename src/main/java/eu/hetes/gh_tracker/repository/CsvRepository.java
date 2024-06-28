package eu.hetes.gh_tracker.repository;

import eu.hetes.gh_tracker.model.EStatus;
import eu.hetes.gh_tracker.model.Issue;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.stream.StreamSupport;

@Repository
@ConditionalOnProperty(prefix = "tracker.repository", name = "type", havingValue = "csv")
class CsvRepository extends BaseFileRepository implements IRepository {
    private static final Logger logger = LoggerFactory.getLogger(CsvRepository.class);

    @Value("${tracker.repository.csv.path:~/gh-tracker.csv}")
    private String csvFilePath;

    private final CSVFormat csvFormatRead = CSVFormat.DEFAULT.builder()
            .setHeader("id", "description", "status", "timestamp", "link", "parent")
            .setSkipHeaderRecord(true)
            .build();

    private final CSVFormat csvFormatWrite = CSVFormat.DEFAULT.builder()
            .setHeader("id", "description", "status", "timestamp", "link", "parent")
            .build();

    @Override
    protected Issue[] readFileContents() throws Exception {
        File file = new File(csvFilePath);
        if (!file.exists()) {
            return new Issue[0];
        }
        Reader reader = new FileReader(file);
        Iterable<CSVRecord> records = csvFormatRead.parse(reader);
        Issue[] data = StreamSupport.stream(records.spliterator(), false)
                .map(csv -> {
                    return new Issue(
                        Long.parseLong(csv.get("id")),
                        Long.parseLong(csv.get("timestamp")),
                        EStatus.valueOf(csv.get("status")),
                        csv.get("description"),
                        csv.get("link"),
                        Long.parseLong(csv.get("parent"))
                    );
                }).toArray(Issue[]::new);
        reader.close();
        return data;
    }

    @Override
    protected void writeFileContents(Issue[] issues) throws Exception {
        CSVPrinter printer = csvFormatWrite.print(new FileWriter(csvFilePath));
        for (Issue issue : issues) {
            printer.printRecord(issue.getId(), issue.getDescription(), issue.getStatus(), issue.getTimestamp(), issue.getLink(), issue.getParentId());
        }
        printer.close(true);
    }
}
