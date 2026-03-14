package com.studiomediatech.bugs.apartition;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "archive")
public class Props {

  private boolean debug = false;

  private int size = 1100;

  private int minBytes = 800;
  private int maxBytes = 2500;
  private int days = 6;

  private int threads = 3;
  private String memoryLimit = "1GB";

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
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

  public int getDays() {
    return days;
  }

  public void setDays(int days) {
    this.days = days;
  }
}
