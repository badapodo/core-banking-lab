package com.corebanking.lab.assignment01;

import com.corebanking.lab.support.PostgreSQLContainerSupport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static com.corebanking.lab.support.AccountFixture.ACCOUNT_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("assignment")
@SpringBootTest
@AutoConfigureMockMvc
class A01Step03Test extends PostgreSQLContainerSupport {

    @Autowired
    MockMvc mockMvc;

    @Test
    void created_account_ids_are_unique() throws Exception {
        Set<String> accountIds = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            String accountId = mockMvc.perform(post(ACCOUNT_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString()
                    .replaceAll(".*\"accountId\":\"([^\"]+)\".*", "$1");

            accountIds.add(accountId);
        }

        assertThat(accountIds).hasSize(5);
    }
}
