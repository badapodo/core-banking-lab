package com.corebanking.lab.assignment10;

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
@Tag("assignment10")
class A10Step01Test extends FinancialLabSupport {
    @Test
    void orchestrator_restart() throws Exception {
        coreFault(503); UUID id=UUID.fromString(saga(200,"restart").path("sagaId").asText());
        awaitSaga(id,"RETRY_PENDING"); orchestrator.restart(); restoreCoreNetwork();
        expect(request(orchestrator,"POST","/api/v1/recovery/run",null,null),200); awaitSaga(id,"COMPLETED");
        conserved(1100); reconciled();
    }
}
