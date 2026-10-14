package com.corebanking.lab.assignment12;

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
@Tag("assignment12")
class A12Step03Test extends FinancialLabSupport {
    @Test
    void metrics() throws Exception {
        JsonNode r=expect(request(core,"POST","/api/v1/workloads",Map.of("seed",7,"accounts",8,"requests",40,"concurrency",4,"initialBalance",1000,"dataset","synthetic"),null),200);
        for(String field: List.of("throughput","p50Millis","p95Millis","p99Millis","failureRate","lockWaitMillis","deadlocks","idempotencyViolations","conservationViolations","ledgerMismatches","recoveryMillis")) {
            assertThat(r.has(field)).as(field).isTrue(); assertThat(r.path(field).asDouble()).isGreaterThanOrEqualTo(0);
        }
        assertThat(r.path("p50Millis").asDouble()).isLessThanOrEqualTo(r.path("p95Millis").asDouble());
        assertThat(r.path("p95Millis").asDouble()).isLessThanOrEqualTo(r.path("p99Millis").asDouble());
        assertThat(r.path("failureRate").asDouble()).isBetween(0.0,1.0);
    }
}
