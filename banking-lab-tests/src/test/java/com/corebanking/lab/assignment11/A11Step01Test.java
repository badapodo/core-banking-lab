package com.corebanking.lab.assignment11;

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
@Tag("assignment11")
class A11Step01Test extends FinancialLabSupport {
    @Test
    void core_reconciliation() throws Exception {
        committed(transfer(200)); sql("UPDATE accounts SET balance=balance+1 WHERE account_id=?",source);
        JsonNode report=expect(request(core,"POST","/api/v1/reconciliation/core",null,null),200);
        assertThat(report.path("status").asText()).isEqualTo("INCONSISTENT"); assertThat(report.path("discrepancies").size()).isGreaterThan(0);
    }
}
