package org.bkd.saas.user;

import static org.bkd.saas.shared.JsonUtils.MAPPER;
import static org.bkd.saas.user.rest.Routes.CREATE_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.dto.RoleEnum;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class CreateUserTests extends AbstractIntegrationTests {

  private static final String EMAIL = "test@test.com";
  private static final String PASSWORD = "S3cretValue!";

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void createUser_returnsCreatedUser() throws Exception {
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);

    postUser(createUserRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.email").value(EMAIL))
        .andExpect(jsonPath("$.role").value(RoleEnum.ROLE_USER.name()))
        .andExpect(jsonPath("$.enabled").value(true))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedAt").isNotEmpty());
  }

  private ResultActions postUser(CreateUserRequest body) throws Exception {
    String content = MAPPER.writeValueAsString(body);

    MockHttpServletRequestBuilder request =
        MockMvcRequestBuilders.post(CREATE_USER).content(content).contentType(APPLICATION_JSON);

    return mockMvc.perform(request);
  }
}
