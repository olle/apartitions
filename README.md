# Apartitions

A small application that does data archiving between PostgreSQL and S3/MinIO,
using the DuckDB `COPY/TO` command, writing gzipped CSV files in
hive-partitioned folders.

This tool was created to help in reproducing the issue described in
https://github.com/duckdb/duckdb/issues/11817.

## Quickstart

**Prerequisites**: Docker or Podman installed on your machine. Java 25+
installed on your machine.

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

The application uses the following configuration properties, either set from
the environment or provided in a Spring Boot configuration.

- `ARCHIVE_SIZE` (int, default: `10000`) - Total number of records to generate
  in the database, for archiving.

- `ARCHIVE_MIN_BYTES` (int, default: `800`) - Minimum size in bytes for each
  generated record. Used to create variability in record sizes (random).

- `ARCHIVE_MAX_BYTES` (int, default: `2500`) - Maximum size in bytes for each
  generated record. Used to create variability in record sizes (random).

- `ARCHIVE_DAYS` (int, default: `7`) - Number of days +/- from the current date
  to spread out the generated records in the database. This creates variability
  in the partition sizes when archiving.

- `ARCHIVE_THREADS` (int, default: `1`) - Number of threads to use in DuckDB for
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

_I've added some simple print statements to [duckdb-java] in an effort to better
 understand what's happening in the `standard_buffer_manager.cpp`, where the
 exception in thrown, and the last record of the allocations looks like this:_

```
...
AllocateTemporaryMemory EXTENSION size: 76.4 MiB used: 944.9 MiB
RegisterMemory EXTENSION size: 76.5 MiB used: 944.9 MiB
EvictBlocksOrThrow EXTENSION size: 76.5 MiB used: 944.9 MiB
MemoryInformation:
	 BASE_TABLE { size: 0 bytes, evicted: 0 bytes}
	 HASH_TABLE { size: 0 bytes, evicted: 0 bytes}
	 PARQUET_READER { size: 0 bytes, evicted: 0 bytes}
	 CSV_READER { size: 0 bytes, evicted: 0 bytes}
	 ORDER_BY { size: 0 bytes, evicted: 0 bytes}
	 ART_INDEX { size: 0 bytes, evicted: 0 bytes}
	 COLUMN_DATA { size: 20.2 MiB, evicted: 2.0 MiB}
	 METADATA { size: 0 bytes, evicted: 0 bytes}
	 OVERFLOW_STRINGS { size: 0 bytes, evicted: 0 bytes}
	 IN_MEMORY_TABLE { size: 0 bytes, evicted: 0 bytes}
	 ALLOCATOR { size: 4.4 MiB, evicted: 0 bytes}
	 EXTENSION { size: 918.0 MiB, evicted: 0 bytes}
	 TRANSACTION { size: 0 bytes, evicted: 0 bytes}
	 EXTERNAL_FILE_CACHE { size: 0 bytes, evicted: 0 bytes}
	 WINDOW { size: 0 bytes, evicted: 0 bytes}
```

  [duckdb-java]: https://github.com/olle/duckdb-java/tree/11817-good-old-printf-debugging


