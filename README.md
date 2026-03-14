# Apartitions

A small application that does data archiving between PostgreSQL and S3/MinIO,
using the DuckDB `COPY/TO` command, writing gzipped CSV files in
hive-partitioned folders.

This tool was created to help in reproducing the issue described in
https://github.com/duckdb/duckdb/issues/11817.

## Quickstart

Prerequisites: Docker or Podman installed on your machine. Java 25+ installed
on your machine.

To get started quickly, use the provided `Makefile` targets.

```sh
make up    ## Start all services (PostgreSQL, MinIO, etc.)
make       ## Builds and runs the Java Spring Boot application, executing the example scenario.
make down  ## Stop services.
```

### Configuration properties

The application uses the following environment variables (prefixed with
`ARCHIVE_`):

- `ARCHIVE_SIZE` (int, default: `1100`) - Total number of records to generate
  in the database, for archiving.

- `ARCHIVE_MIN_BYTES` (int, default: `800`) - Minimum size in bytes for each
  generated record. Used to create variability in record sizes (random).

- `ARCHIVE_MAX_BYTES` (int, default: `2500`) - Maximum size in bytes for each
  generated record. Used to create variability in record sizes (random).

- `ARCHIVE_DAYS` (int, default: `6`) - Number of days +/- from the current date
  to spread out the generated records in the database. This creates variability
  in the partition sizes when archiving.

- `ARCHIVE_THREADS` (int, default: `3`) - Number of threads to use in DuckDB for
  parallel processing. https://duckdb.org/docs/stable/configuration/overview#global-configuration-options

- `ARCHIVE_MEMORY_LIMIT` (string, default: `"1GB"`) - Memory limit to use in 
  DuckDB for the archiving process. https://duckdb.org/docs/stable/configuration/overview#global-configuration-options

- `ARCHIVE_DEBUG` (boolean, default: `false`) - Enable debug logging in DuckDB.

