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

- `ARCHIVE_DEBUG` (boolean, default: `false`) - Enable debug logging for
  archiving operations.

- `ARCHIVE_SIZE` (int, default: `1100`) - Target number of records per
  partition.

- `ARCHIVE_BATCH` (int, default: `200`) - Number of records to process in each
  batch.

- `ARCHIVE_MIN_BYTES` (int, default: `800`) - Minimum file size in bytes for
  archiving.

- `ARCHIVE_MAX_BYTES` (int, default: `2500`) - Maximum file size in bytes for
  archiving.

- `ARCHIVE_DAYS` (int, default: `6`) - Number of days to retain archived data.

- `ARCHIVE_THREADS` (int, default: `5`) - Number of threads for parallel
  processing.

- `ARCHIVE_MEMORY_LIMIT` (string, default: `"1GB"`) - Memory limit for the
  archiving process.

