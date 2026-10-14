package com.corebanking.lab.assignment07;

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
@Tag("assignment07")
class A07Step06Test extends FinancialLabSupport {
    @Test
    void duplicate_saga() throws Exception {
        List<HttpResponse<String>> results = concurrent(10, i -> () -> request(orchestrator, "POST", "/api/v1/sagas", command(source, target, 200), "same-saga"));
        Set<String> ids = new HashSet<>(); for (HttpResponse<String> r : results) ids.add(expect(r, 202).path("sagaId").asText());
        assertThat(ids).hasSize(1); awaitSaga(UUID.fromString(ids.iterator().next()), "COMPLETED");
        assertThat(balance(source)).isEqualTo(800);
    }
}
