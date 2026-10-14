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
class A11Step02Test extends FinancialLabSupport {
    @Test
    void missing_outbox() throws Exception {
        UUID id=committed(transfer(200)); sql("DELETE FROM outbox_events WHERE transfer_id=?",id);
        JsonNode report=expect(request(projections,"POST","/api/v1/reconciliation/distributed",null,null),200);
        assertThat(report.path("discrepancies").toString()).contains("MISSING_OUTBOX",id.toString());
    }
}
