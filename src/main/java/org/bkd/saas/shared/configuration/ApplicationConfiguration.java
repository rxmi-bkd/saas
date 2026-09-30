package org.bkd.saas.shared.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApplicationConfiguration {

  @Bean
  public RestClient restClient() {
    return RestClient.create();
  }
}
