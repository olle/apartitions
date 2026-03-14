package com.studiomediatech.bugs.apartition;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableConfigurationProperties(Props.class)
public class ApartitionApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApartitionApplication.class, args);
  }

  @Autowired private DbRepository db;
  @Autowired private ArchiveRepository archive;
  @Autowired private Props props;

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    var start = System.currentTimeMillis();
    System.out.println("STARTING...");
    db.init();
    db.createDataFixtures(props);
    db.report();
    System.out.println(">> " + Duration.ofMillis(System.currentTimeMillis() - start));
    archive.archiveData(props);
    System.out.println("DONE " + Duration.ofMillis(System.currentTimeMillis() - start));
    archive.report();
  }
}
