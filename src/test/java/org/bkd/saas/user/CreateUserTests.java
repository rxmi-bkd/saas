package org.bkd.saas.user;

import static org.bkd.saas.shared.JsonUtils.MAPPER;
import static org.bkd.saas.user.rest.Routes.CREATE_USER;

import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class CreateUserTests extends AbstractIntegrationTests {

  @Autowired private MockMvc mockMvc;

  @Test
  void testGetUser() throws Exception {
    RequestBuilder request =
        MockMvcRequestBuilders.post(CREATE_USER)
            .content(MAPPER.writeValueAsString(new CreateUserRequest("t", "t")))
            .contentType(MediaType.APPLICATION_JSON);
    ResultMatcher isOk = MockMvcResultMatchers.status().isOk();
    ResultMatcher jsonResponse =
        MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON);
    mockMvc.perform(request).andExpect(isOk).andExpect(jsonResponse).andReturn();
  }
}
