package com.corebanking.lab.assignment03;

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
@Tag("assignment03")
class A03Step05Test extends FinancialLabSupport {
    @Test
    void history() throws Exception {
        UUID id = committed(transfer(200));
        JsonNode entries = expect(request(core, "GET", "/api/v1/accounts/" + source + "/ledger", null, null), 200);
        assertThat(entries.isArray()).isTrue();
        assertThat(entries.size()).isEqualTo(1);
        assertThat(entries.get(0).path("transferId").asText()).isEqualTo(id.toString());
        assertThat(entries.get(0).path("side").asText()).isEqualTo("DEBIT");
    }
}
