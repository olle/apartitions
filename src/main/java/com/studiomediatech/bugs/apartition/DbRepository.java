package com.studiomediatech.bugs.apartition;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;
import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
class DbRepository {

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
  public void createDataFixtures(Props props) {

    int batches = props.getBatches();
    int batchSize = props.getBatchSize();

    System.out.println(
        "GENERATING %d TEST DATA ENTRIES IN BATCHES OF %d"
            .formatted(batches * batchSize, batchSize));

    var total = new LongAdder();
    for (AtomicInteger i = new AtomicInteger(); i.getAndIncrement() < batches; ) {
      var batch =
          IntStream.range(0, batchSize)
              .mapToObj(_ -> generate(props, total))
              .map(value -> Map.of("value", value))
              .toList();

      jdbc.batchUpdate(
          "INSERT INTO data (value) VALUES (:value)", SqlParameterSourceUtils.createBatch(batch));
      System.out.print(".");
    }

    System.out.println("");
    System.out.println(
        "DONE, GENERATED %s TEST FIXTURE DATA"
            .formatted(FileUtils.byteCountToDisplaySize(total.sum())));
  }

  private String generate(Props props, LongAdder total) {

    ThreadLocalRandom random = ThreadLocalRandom.current();
    int size = random.nextInt(props.getMinBytes(), props.getMaxBytes());
    total.add(size);

    return random
        .ints('a', 'z' + 1)
        .limit(size)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
