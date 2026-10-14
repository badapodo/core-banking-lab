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
class A07Step08Test extends FinancialLabSupport {
    @Test
    void resume_pending() throws Exception {
        coreFault(503); UUID id = UUID.fromString(saga(200, "resume").path("sagaId").asText());
        awaitSaga(id, "RETRY_PENDING"); orchestrator.crash(); restoreCoreNetwork(); orchestrator.start();
        expect(request(orchestrator, "POST", "/api/v1/recovery/run", null, null), 200);
        awaitSaga(id, "COMPLETED"); assertThat(balance(source)).isEqualTo(800); reconciled();
    }
}
