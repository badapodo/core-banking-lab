package com.corebanking.lab.assignment04;

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
@Tag("assignment04")
class A04Step06Test extends FinancialLabSupport {
    @Test
    void contention_recovery() throws Exception {
        List<HttpResponse<String>> results = concurrent(30, i -> () -> i % 2 == 0 ? transfer(1) : transfer(core, target, source, 1, null));
        for (HttpResponse<String> r : results) { if (r.statusCode() == 201) committed(r); else error(r, 409, "RETRYABLE_CONFLICT"); }
        committed(transfer(1));
        conserved(1100); reconciled();
    }
}
