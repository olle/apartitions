package com.studiomediatech.bugs.apartition;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
class DbRepository {

  private static final int BATCH_SIZE = 300;

  private final NamedParameterJdbcTemplate jdbc;

  public DbRepository(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Transactional
  public void init() {
    jdbc.update(
        """
				CREATE TABLE IF NOT EXISTS data (
				    id SERIAL PRIMARY KEY
				  , created TIMESTAMPTZ NOT NULL DEFAULT NOW()
				  , value TEXT NOT NULL
				);
				TRUNCATE data RESTART IDENTITY;
				""",
        Map.of());
  }

  @Transactional
  public void createData(Props props) {

    System.out.println(
        "GENERATING %d TEST DATA ENTRIES SPREAD OVER %d DAY(S)."
            .formatted(props.getSize(), props.getDays()));

    int entries = props.getSize();

    while (entries > 0) {
      int nextBatch = entries < BATCH_SIZE ? entries : BATCH_SIZE;

      jdbc.batchUpdate(
          "INSERT INTO data (created, value) VALUES ((NOW() + :days::INTERVAL)::TIMESTAMPTZ, :value)",
          SqlParameterSourceUtils.createBatch(
              IntStream.range(0, nextBatch).mapToObj(_ -> generate(props)).toList()));

      System.out.print(".");
      entries = Math.max(entries - BATCH_SIZE, 0);
    }

    System.out.println("");
  }

  private Map<String, Object> generate(Props props) {

    ThreadLocalRandom random = ThreadLocalRandom.current();

    int size = random.nextInt(props.getMinBytes(), props.getMaxBytes());
    int day = random.nextInt(-1 * props.getDays() / 2, props.getDays() / 2);

    String value =
        random
            .ints('a', 'z' + 1)
            .limit(size)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();

    return Map.of("value", value, "days", "'%d days'".formatted(day));
  }

  @Transactional(readOnly = true)
  public void report() {

    var total =
        (Number)
            jdbc.queryForList(
                    """
				WITH stats AS (
				  SELECT AVG(pg_column_size(value)) AS avg_value_size
				  FROM data
				  LIMIT 1000
				)
				SELECT
				  (COUNT(*) * (SELECT avg_value_size FROM stats))::INT AS total_size
				FROM data
				""",
                    Map.of())
                .iterator()
                .next()
                .get("total_size");

    System.out.println(
        "DONE, GENERATED %s TEST FIXTURE DATA".formatted(FileUtils.byteCountToDisplaySize(total)));
  }
}
