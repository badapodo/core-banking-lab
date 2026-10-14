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
class A05Step06Test extends FinancialLabSupport {
    @Test
    void idempotency_atomicity() throws Exception {
        rejectWrites("idempotency_records");
        error(transfer(core, source, target, 200, "atomic"), 503, "FINANCIAL_WRITE_FAILED");
        unchanged(); assertThat(scalar("SELECT count(*) FROM idempotency_records")).isZero();
    }
}
