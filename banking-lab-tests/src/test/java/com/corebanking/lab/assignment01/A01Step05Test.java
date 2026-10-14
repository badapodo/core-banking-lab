package com.corebanking.lab.assignment01;

import com.corebanking.lab.support.PostgreSQLContainerSupport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.corebanking.lab.support.AccountFixture.ACCOUNT_API;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("assignment")
@SpringBootTest
@AutoConfigureMockMvc
class A01Step05Test extends PostgreSQLContainerSupport {

    @Autowired
    MockMvc mockMvc;

    @Test
    void unknown_account_returns_404() throws Exception {
        mockMvc.perform(get(ACCOUNT_API + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
