package com.corebanking.lab.assignment06;

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
@Tag("assignment06")
class A06Step02Test extends FinancialLabSupport {
    @Test
    void http_boundary() throws Exception {
        committed(transfer(orchestrator, source, target, 200, "http"));
        gateway.verify(postRequestedFor(urlEqualTo("/api/v1/transfers")).withHeader("Idempotency-Key", equalTo("http")));
        conserved(1100);
    }
}
