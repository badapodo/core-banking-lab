package com.corebanking.lab.assignment11;

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
@Tag("assignment11")
class A11Step05Test extends FinancialLabSupport {
    @Test
    void duplicate_effect() throws Exception {
        UUID id=committed(transfer(200)), eventId=outboxEventId(id); awaitEffect("audit",eventId,1); awaitConsumerOffset("audit");
        projectionSql("UPDATE consumed_events SET effect_count=2 WHERE consumer='audit' AND event_id=?",eventId);
        JsonNode report=expect(request(projections,"POST","/api/v1/reconciliation/distributed",null,null),200);
        assertThat(report.path("discrepancies").toString()).contains("DUPLICATE_EFFECT",eventId.toString()); ledgerPair(id);
    }
}
