package com.corebanking.lab.assignment02;

import com.corebanking.lab.support.FinancialLabSupport;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.http.HttpResponse;
import java.sql.*;
import java.time.Duration;
import java.util.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Tag("assignment")
@Tag("assignment02")
class A02Step07Test extends FinancialLabSupport {
    @Test
    void fund_conservation() throws Exception {
        for (int i = 0; i < 5; i++) committed(transfer(100));
        assertThat(balance(source)).isEqualTo(500);
        assertThat(balance(target)).isEqualTo(600);
        conserved(1100);
    }
}
