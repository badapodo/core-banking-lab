package com.corebanking.lab.assignment01;

import com.jayway.jsonpath.JsonPath;
import com.corebanking.lab.support.PostgreSQLContainerSupport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.corebanking.lab.support.AccountFixture.ACCOUNT_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Tag("assignment")
@SpringBootTest
@AutoConfigureMockMvc
class A01Step01Test extends PostgreSQLContainerSupport {

    @Autowired
    MockMvc mockMvc;

    @Test
    void create_account_returns_201_and_account_id() throws Exception {
        MvcResult result = mockMvc.perform(post(ACCOUNT_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andReturn();

        assertThat(result.getResponse().getStatus())
                .as("expected account creation to succeed; response body was: %s",
                        result.getResponse().getContentAsString())
                .isEqualTo(201);

        String accountId = JsonPath.read(result.getResponse().getContentAsString(), "$.accountId");

        assertThat(accountId).isNotBlank();
    }
}
