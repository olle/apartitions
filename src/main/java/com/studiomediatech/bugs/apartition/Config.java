package com.studiomediatech.bugs.apartition;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.duckdb.DuckDBConnection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.AbstractDataSource;

@Configuration
@EnableConfigurationProperties(Props.class)
class Config {

  @Bean
  @Primary
  public DataSourceProperties dataSourceProperties() {
    var props = new DataSourceProperties();
    props.setUrl("jdbc:postgresql://localhost:5432/postgres");
    props.setUsername("postgres");
    props.setPassword("password");
    return props;
  }

  @Bean
  public Properties minioProperties() {
    var props = new Properties();
    props.setProperty("key", "minio");
    props.setProperty("secret", "password");
    props.setProperty("endpoint", "localhost:9000");
    return props;
  }

  @Bean
  @Primary
  public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
    return new NamedParameterJdbcTemplate(dataSource);
  }

  @Bean
  public DuckDBConnection duckDbConnection(
      Props props, @Qualifier("minioProperties") Properties minio, DataSourceProperties db) {
    try {
      DuckDBConnection conn = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
      conn.createStatement()
          .execute(
"""
-- Configure thread/memory limits
SET GLOBAL threads=%d;
SET GLOBAL memory_limit='%s';
-- Connect to local MinIO/S3
INSTALL httpfs;
LOAD httpfs;
CREATE TEMPORARY SECRET IF NOT EXISTS minio (
  TYPE s3,
  KEY_ID '%s',
  SECRET '%s',
  ENDPOINT 'localhost:9000',
  USE_SSL false,
  URL_STYLE path
);
-- Connect to local PostgreSQL
INSTALL postgres;
LOAD postgres;
CREATE TEMPORARY SECRET IF NOT EXISTS postgresql (
  TYPE postgres,
  HOST 'localhost',
  PORT 5432,
  DATABASE 'postgres',
  USER '%s',
  PASSWORD '%s'
);
ATTACH IF NOT EXISTS '' AS db (TYPE postgres, SECRET postgresql);
"""
                  .formatted(
                      props.getThreads(),
                      props.getMemoryLimit(),
                      minio.getProperty("key"),
                      minio.getProperty("secret"),
                      db.getUsername(),
                      db.getPassword()));

      if (props.isDebug()) {
        conn.createStatement().execute("CALL enable_logging(level = 'debug', storage = 'stdout');");
      }

      return conn;
    } catch (SQLException e) {
      throw new IllegalStateException("Failed to create DuckDB connection.", e);
    }
  }

  @Bean
  public DuckDBJdbcTemplate duckDBJdbcTemplate(DuckDBConnection duckDBConnection) {
    return new DuckDBJdbcTemplate(new DuckDBDataSource(duckDBConnection));
  }

  public class DuckDBDataSource extends AbstractDataSource {

    private final DuckDBConnection duckDBConnection;

    public DuckDBDataSource(DuckDBConnection duckDBConnection) {
      this.duckDBConnection = duckDBConnection;
    }

    @Override
    public Connection getConnection() throws SQLException {
      return duckDBConnection.duplicate();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
      return duckDBConnection.duplicate();
    }
  }

  public class DuckDBJdbcTemplate extends NamedParameterJdbcTemplate {
    public DuckDBJdbcTemplate(DuckDBDataSource duckDBDataSource) {
      super(duckDBDataSource);
    }
  }
}
