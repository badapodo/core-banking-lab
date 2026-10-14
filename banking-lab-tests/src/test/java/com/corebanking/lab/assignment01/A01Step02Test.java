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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("assignment")
@SpringBootTest
@AutoConfigureMockMvc
class A01Step02Test extends PostgreSQLContainerSupport {

    @Autowired
    MockMvc mockMvc;

    @Test
    void created_account_starts_with_zero_balance_and_active_status() throws Exception {
        mockMvc.perform(post(ACCOUNT_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
