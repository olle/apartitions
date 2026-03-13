package com.studiomediatech.bugs.apartition;

import com.studiomediatech.bugs.apartition.Config.DuckDBJdbcTemplate;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
class ArchiveRepository {

  private final DuckDBJdbcTemplate duckDb;

  public ArchiveRepository(DuckDBJdbcTemplate duckDb) {
    this.duckDb = duckDb;
  }

  public void archiveData() {
    showDatabaseInfoAndExists();
    copyPartitionedData();
  }

  private void showDatabaseInfoAndExists() {
    System.out.println(duckDb.queryForList("PRAGMA database_size;", Map.of()));
    System.out.println(
        duckDb.queryForList(
"""
SELECT ((SELECT COUNT(*) FROM glob('s3://archive/data/*/*/*/*.csv.gz')) > 0) AS archived_data_exists
;
""",
            Map.of()));
  }

  private void copyPartitionedData() {
    duckDb.update(
"""
COPY
  (
   SELECT
    DATE_PART('year', created) AS year
  , DATE_PART('month', created) AS month
  , created::DATE AS "date"
  , *
   FROM db.data
  )
      TO 's3://archive/data'
  (
   FORMAT csv,
   PARTITION_BY (year, month, date),
   APPEND true,
   COMPRESSION gzip,
   FILENAME_PATTERN 'data_{uuidv7}',
   FILE_EXTENSION 'csv.gz'
  )
;
""",
        Map.of());
  }
}
