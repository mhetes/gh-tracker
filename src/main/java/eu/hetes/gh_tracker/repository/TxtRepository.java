package eu.hetes.gh_tracker.repository;

import eu.hetes.gh_tracker.model.EStatus;
import eu.hetes.gh_tracker.model.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

@Repository
@ConditionalOnProperty(prefix = "tracker.repository", name = "type", havingValue = "txt")
public class TxtRepository extends BaseFileRepository implements IRepository {
    private static final Logger logger = LoggerFactory.getLogger(TxtRepository.class);

    @Value("${tracker.repository.txt.path:gh-tracker.txt}")
    private String txtFile;

    @Value("${tracker.repository.txt.separator:#}")
    private String separator;

    @Override
    protected Issue[] readFileContents() throws Exception {
        File file = new File(txtFile);
        if (!file.exists()) {
            return new Issue[0];
        }
        ArrayList<Issue> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(txtFile));
        String line = reader.readLine();
        while (line != null) {
            String[] fields = line.split(separator);
            if (fields.length == 6) {
                list.add(new Issue(
                    Long.parseLong(fields[0]), // id
                    Long.parseLong(fields[3]), // timestamp
                    EStatus.valueOf(fields[2]), // status
                    fields[1], // description
                    fields[4], // link
                    Long.parseLong(fields[5]) // parent
                ));
            }
            line = reader.readLine();
        }
        reader.close();
        return list.toArray(Issue[]::new);
    }

    @Override
    protected void writeFileContents(Issue[] issues) throws Exception {
        FileWriter writer = new FileWriter(txtFile);
        for (Issue issue : issues) {
            writer.write(
                issue.getId() + separator +
                    issue.getDescription() + separator +
                    issue.getStatus() + separator +
                    issue.getTimestamp() + separator +
                    issue.getLink() + separator +
                    issue.getParentId() + System.lineSeparator()
            );
        }
        writer.close();
    }
}
