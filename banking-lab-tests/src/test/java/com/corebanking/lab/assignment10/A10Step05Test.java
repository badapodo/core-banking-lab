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
class A10Step05Test extends FinancialLabSupport {
    @Test
    void mid_execution_crash() throws Exception {
        holdResponseAfterCommit(); UUID id=UUID.fromString(saga(200,"crash").path("sagaId").asText());
        awaitCommittedResponse(); assertThat(balance(source)).isEqualTo(800);
        orchestrator.crash(); releaseResponse(); orchestrator.start();
        expect(request(orchestrator,"POST","/api/v1/recovery/run",null,null),200); awaitSaga(id,"COMPLETED");
        assertThat(scalar("SELECT count(*) FROM transfers WHERE status='COMMITTED'")).isEqualTo(1); reconciled();
    }
}
