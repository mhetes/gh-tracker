package eu.hetes.gh_tracker.repository;

import eu.hetes.gh_tracker.model.EStatus;
import eu.hetes.gh_tracker.model.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;

@Repository
@ConditionalOnProperty(prefix = "tracker.repository", name = "type", havingValue = "mysql")
public class MySqlRepository implements IRepository {
    private static final Logger logger = LoggerFactory.getLogger(MySqlRepository.class);

    @Value("${tracker.repository.mysql.server}")
    private String server;

    @Value("${tracker.repository.mysql.login}")
    private String login;

    @Value("${tracker.repository.mysql.password}")
    private String password;

    @Value("${tracker.repository.mysql.db}")
    private String database;

    private Connection connection = null;
    private final String insertSql = "INSERT INTO Bugs (description, status, timestamp, link, parent) VALUES (?, ?, ?, ?, ?)";
    private final String getOneSql = "SELECT id, description, status, timestamp, link, parent FROM Bugs WHERE id = ?";
    private final String updateSql = "UPDATE Bugs SET description = ?, status = ?, timestamp = ?, link = ?, parent = ? WHERE id = ?";
    private final String select1 = "SELECT id, description, status, timestamp, link, parent FROM Bugs";
    private final String select2 = "SELECT id, description, status, timestamp, link, parent FROM Bugs WHERE status = ?";

    @Override
    public void connect() throws Exception {
        connection = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database, login, password);
    }

    @Override
    public void disconnect() throws Exception {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    @Override
    public Issue createIssue(Issue issue) throws Exception {
        // Check for connected status
        if (connection == null) throw new Exception("Database connection not opened");

        PreparedStatement stmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, issue.getDescription());
        stmt.setString(2, issue.getStatus().toString());
        stmt.setLong(3, issue.getTimestamp());
        stmt.setString(4, issue.getLink());
        stmt.setLong(5, issue.getParentId());
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            long newId = rs.getLong(1);
            return new Issue(newId, issue);
        }
        return null;
    }

    @Override
    public Issue getIssue(long id) throws Exception {
        // Check for connected status
        if (connection == null) throw new Exception("Database connection not opened");

        PreparedStatement stmt = connection.prepareStatement(getOneSql);
        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Issue(
                rs.getLong(1),
                rs.getLong(4),
                EStatus.valueOf(rs.getString(3)),
                rs.getString(2),
                rs.getString(5),
                rs.getLong(6)
            );
        }
        return null;
    }

    @Override
    public Issue updateIssue(Issue issue) throws Exception {
        // Check for connected status
        if (connection == null) throw new Exception("Database connection not opened");

        PreparedStatement stmt = connection.prepareStatement(updateSql);
        stmt.setString(1, issue.getDescription());
        stmt.setString(2, issue.getStatus().toString());
        stmt.setLong(3, issue.getTimestamp());
        stmt.setString(4, issue.getLink());
        stmt.setLong(5, issue.getParentId());
        stmt.setLong(6, issue.getId());
        stmt.executeUpdate();
        if (stmt.getUpdateCount() > 0) {
            return issue.clone();
        }
        return null;
    }

    @Override
    public Issue[] getIssuesByStatus(EStatus status) throws Exception {
        // Check for connected status
        if (connection == null) throw new Exception("Database connection not opened");

        PreparedStatement stmt;
        if (status == null) {
            stmt = connection.prepareStatement(select1);
        } else {
            stmt = connection.prepareStatement(select2);
            stmt.setString(1, status.toString());
        }
        ResultSet rs = stmt.executeQuery();
        ArrayList<Issue> list = new ArrayList<>();
        while (rs.next()) {
            Issue issue = new Issue(
                rs.getLong(1),
                rs.getLong(4),
                EStatus.valueOf(rs.getString(3)),
                rs.getString(2),
                rs.getString(5),
                rs.getLong(6)
            );
            list.add(issue);
        }
        return list.toArray(Issue[]::new);
    }
}
