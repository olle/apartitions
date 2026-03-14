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
make up    ## Start all containers
make       ## Builds and runs the app (default example scenario)
```

Any consecutive invocation of `make` will clean the target S3 bucket and run
the demo scenario again.

Stop all the containers with `make down` or `make clean` to also remove the
volumes.

### Configuration properties

The application uses the following environment variables (prefixed with
`ARCHIVE_`):

- `ARCHIVE_SIZE` (int, default: `10000`) - Total number of records to generate
  in the database, for archiving.

- `ARCHIVE_MIN_BYTES` (int, default: `800`) - Minimum size in bytes for each
  generated record. Used to create variability in record sizes (random).

- `ARCHIVE_MAX_BYTES` (int, default: `2500`) - Maximum size in bytes for each
  generated record. Used to create variability in record sizes (random).

- `ARCHIVE_DAYS` (int, default: `7`) - Number of days +/- from the current date
  to spread out the generated records in the database. This creates variability
  in the partition sizes when archiving.

- `ARCHIVE_THREADS` (int, default: `3`) - Number of threads to use in DuckDB for
  parallel processing. https://duckdb.org/docs/stable/configuration/overview#global-configuration-options

- `ARCHIVE_MEMORY_LIMIT` (string, default: `"1GB"`) - Memory limit to use in 
  DuckDB for the archiving process. https://duckdb.org/docs/stable/configuration/overview#global-configuration-options

- `ARCHIVE_DEBUG` (boolean, default: `false`) - Enable debug logging in DuckDB.

## Example scenario

### `Out of Memory Error: could not allocate block of size 76.5 MiB (944.1 MiB/953.6 MiB used)`

This tests creates around 15MB of data in the database, spread over 30 days, and
when the archiving process runs, it will hit the "Out of Memory Error" in
DuckDB, due to the default memory limit of 1GB being exceeded by the size of the
data being processed.

https://github.com/duckdb/duckdb/issues/11817.

```sh
make up
ARCHIVE_DAYS=30 make
```
