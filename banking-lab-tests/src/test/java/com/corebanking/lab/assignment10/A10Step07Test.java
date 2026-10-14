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
class A10Step07Test extends FinancialLabSupport {
    @Test
    void retry_exhaustion() throws Exception {
        coreFault(503); UUID id=UUID.fromString(saga(200,"exhaust").path("sagaId").asText());
        for(int i=0;i<4;i++) expect(request(orchestrator,"POST","/api/v1/recovery/run",null,null),200);
        awaitSaga(id,"MANUAL_REVIEW"); assertThat(sagaState(id).path("attempts").asInt()).isGreaterThanOrEqualTo(3); unchanged();
    }
}
