package org.bkd.saas;

import jakarta.annotation.PostConstruct;
import org.bkd.saas.security.SecurityTestClient;
import org.bkd.saas.user.UserTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SaasApplication.class)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTests {
  @LocalServerPort private int port;

  @ServiceConnection
  private static final PostgreSQLContainer postgresql =
      new PostgreSQLContainer("postgres:17-alpine");

  protected SecurityTestClient securityClient;
  protected UserTestClient userClient;


  @PostConstruct
  void postConstruct() {
    RestClient restClient = RestClient.builder().baseUrl(host()).build();
    securityClient = new SecurityTestClient(restClient);
    userClient = new UserTestClient(restClient);
  }

  private String host() {
    return "http://localhost:" + port;
  }
}
