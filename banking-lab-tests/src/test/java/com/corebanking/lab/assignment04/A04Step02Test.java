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
class A04Step02Test extends FinancialLabSupport {
    @Test
    void overdraw_competition() throws Exception {
        List<HttpResponse<String>> results = concurrent(20, i -> () -> transfer(100));
        long successes = results.stream().filter(r -> r.statusCode() == 201).count();
        assertThat(successes).isEqualTo(10);
        for (HttpResponse<String> r : results) { if (r.statusCode() == 201) committed(r); else error(r, 409, "INSUFFICIENT_FUNDS"); }
        assertThat(balance(source)).isZero(); conserved(1100); reconciled();
    }
}
