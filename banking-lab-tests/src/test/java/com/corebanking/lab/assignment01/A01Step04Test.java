package com.corebanking.lab.assignment01;

import com.corebanking.lab.support.PostgreSQLContainerSupport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.corebanking.lab.support.AccountFixture.ACCOUNT_API;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("assignment")
@SpringBootTest
@AutoConfigureMockMvc
class A01Step04Test extends PostgreSQLContainerSupport {

    @Autowired
    MockMvc mockMvc;

    @Test
    void created_account_can_be_loaded_by_id() throws Exception {
        String accountId = mockMvc.perform(post(ACCOUNT_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"accountId\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get(ACCOUNT_API + "/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.accountId", notNullValue()));
    }
}
