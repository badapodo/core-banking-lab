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
class A11Step07Test extends FinancialLabSupport {
    @Test
    void repeat_reconciliation() throws Exception {
        UUID id=committed(transfer(200)); UUID eventId=outboxEventId(id);
        awaitEffect("audit",eventId,1); awaitEffect("notifications",eventId,1); awaitConsumerOffset("audit"); awaitConsumerOffset("notifications");
        expect(request(projections,"POST","/api/v1/reconciliation/distributed?repair=true",null,null),200);
        JsonNode again=expect(request(projections,"POST","/api/v1/reconciliation/distributed?repair=true",null,null),200);
        assertThat(again.path("repaired").asInt()).isZero(); ledgerPair(id); conserved(1100);
    }
}
