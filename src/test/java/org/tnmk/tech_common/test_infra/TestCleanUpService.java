package org.tnmk.tech_common.test_infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Fail;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestCleanUpService {

//  private final JdbcTemplate jdbcTemplate;

  private final DataSource datasource;
//  public void cleanUpData() {
//    String sql = """
//        DROP ALL OBJECTS
//      """;
//    jdbcTemplate.execute(sql);
//    log.info("Clean up data successfully!");
//  }

  public void cleanUpData() {
    try {
      clearDatabase();
    } catch (Exception e) {
      Fail.fail(e.getMessage());
    }
  }

  public void clearDatabase() throws SQLException {
    Connection c = datasource.getConnection();
    Statement s = c.createStatement();

    // Disable FK
    s.execute("SET REFERENTIAL_INTEGRITY FALSE");

    // Find all tables and truncate them
    Set<String> tables = new HashSet<String>();
    ResultSet rs = s.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
    while (rs.next()) {
      tables.add(rs.getString(1));
    }
    rs.close();
    for (String table : tables) {
      s.executeUpdate("TRUNCATE TABLE " + table);
    }

    // Idem for sequences
    Set<String> sequences = new HashSet<String>();
    rs = s.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'");
    while (rs.next()) {
      sequences.add(rs.getString(1));
    }
    rs.close();
    for (String seq : sequences) {
      s.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
    }

    // Enable FK
    s.execute("SET REFERENTIAL_INTEGRITY TRUE");
    s.close();
    c.close();
  }
}
