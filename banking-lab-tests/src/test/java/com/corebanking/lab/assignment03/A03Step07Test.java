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
class A03Step07Test extends FinancialLabSupport {
    @Test
    void reconciliation() throws Exception {
        committed(transfer(200));
        JsonNode report = expect(request(core, "POST", "/api/v1/reconciliation/core", null, null), 200);
        assertThat(report.path("status").asText()).isEqualTo("CONSISTENT");
        assertThat(report.path("discrepancies").size()).isZero();
        reconciled();
    }
}
