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
class A11Step03Test extends FinancialLabSupport {
    @Test
    void missing_consumption() throws Exception {
        UUID id=committed(transfer(200)); UUID eventId=outboxEventId(id);
        awaitEffect("audit",eventId,1); awaitEffect("notifications",eventId,1);
        awaitConsumerOffset("audit"); awaitConsumerOffset("notifications"); projections.crash();
        projectionSql("DELETE FROM consumed_events WHERE transfer_id=?",id); projections.start();
        JsonNode report=expect(request(projections,"POST","/api/v1/reconciliation/distributed",null,null),200);
        assertThat(report.path("discrepancies").toString()).contains("MISSING_CONSUMPTION",id.toString());
    }
}
