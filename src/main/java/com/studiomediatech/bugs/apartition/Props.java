package com.studiomediatech.bugs.apartition;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "archive")
public class Props {

  private boolean debug = false;
  private int minBytes = 1400;
  private int maxBytes = 2000;
  private int batchSize = 100;
  private int batches = 100;
  private int threads = 3;
  private String memoryLimit = "1GB";

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public int getMinBytes() {
    return minBytes;
  }

  public void setMinBytes(int minBytes) {
    this.minBytes = minBytes;
  }

  public int getMaxBytes() {
    return maxBytes;
  }

  public void setMaxBytes(int maxBytes) {
    this.maxBytes = maxBytes;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public int getBatches() {
    return batches;
  }

  public void setBatches(int batches) {
    this.batches = batches;
  }

  public int getThreads() {
    return threads;
  }

  public void setThreads(int threads) {
    this.threads = threads;
  }

  public String getMemoryLimit() {
    return memoryLimit;
  }

  public void setMemoryLimit(String memoryLimit) {
    this.memoryLimit = memoryLimit;
  }
}
