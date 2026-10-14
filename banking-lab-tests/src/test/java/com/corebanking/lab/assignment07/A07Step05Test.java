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
class A07Step05Test extends FinancialLabSupport {
    @Test
    void uncertain_commit() throws Exception {
        loseResponseAfterCommit();
        UUID id = UUID.fromString(saga(200, "uncertain").path("sagaId").asText());
        expect(request(orchestrator, "POST", "/api/v1/recovery/run", null, null), 200);
        awaitSaga(id, "COMPLETED"); assertThat(balance(source)).isEqualTo(800);
        assertThat(scalar("SELECT count(*) FROM transfers WHERE status='COMMITTED'")).isEqualTo(1); reconciled();
    }
}
