package com.hei.note.mail;

import com.hei.note.PojaGenerated;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@PojaGenerated
@Configuration
public class EmailConf {

  @Getter private final String sesSource;
  private final Region region;

  public EmailConf(
      @Value("${aws.ses.source:noreply@poja.io}") String sesSource,
      @Value("${aws.region:eu-west-3}") String region) {
    this.sesSource = sesSource;
    this.region = Region.of(region);
  }

  @Bean
  public SesClient getSesClient() {
    return SesClient.builder().region(region).build();
  }
}
