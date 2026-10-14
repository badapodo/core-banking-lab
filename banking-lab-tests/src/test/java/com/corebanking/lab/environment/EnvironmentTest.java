package com.corebanking.lab.environment;

import com.corebanking.lab.support.PostgreSQLContainerSupport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static com.corebanking.lab.support.AccountFixture.ACCOUNT_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("environment")
@SpringBootTest
@AutoConfigureMockMvc
class EnvironmentTest extends PostgreSQLContainerSupport {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void spring_context_loads_with_postgresql_and_flyway() {
        Integer accountTableCount = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables where table_name = 'accounts'",
                Integer.class
        );

        assertThat(accountTableCount).isEqualTo(1);
    }

    @Test
    void invalid_uuid_returns_400_without_invoking_assignment_logic() throws Exception {
        mockMvc.perform(get(ACCOUNT_API + "/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }
}
