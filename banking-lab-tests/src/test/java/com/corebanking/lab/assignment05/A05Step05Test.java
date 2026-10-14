package com.corebanking.lab.assignment05;

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
@Tag("assignment05")
class A05Step05Test extends FinancialLabSupport {
    @Test
    void restart_retry() throws Exception {
        HttpResponse<String> first = transfer(core, source, target, 200, "restart");
        UUID id = committed(first); core.restart();
        HttpResponse<String> retry = transfer(core, source, target, 200, "restart");
        assertThat(committed(retry)).isEqualTo(id); assertThat(retry.body()).isEqualTo(first.body());
        assertThat(balance(source)).isEqualTo(800);
    }
}
