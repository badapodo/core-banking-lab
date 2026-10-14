package com.corebanking.lab.assignment08;

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
@Tag("assignment08")
class A08Step05Test extends FinancialLabSupport {
    @Test
    void redelivery() throws Exception {
        UUID id = committed(transfer(200)), eventId = UUID.randomUUID();
        projections.crash(); sendEvent(event(eventId, id, 1)); projections.start();
        awaitEffect("audit", eventId, 1); assertThat(projectionCount("audit", eventId)).isEqualTo(1);
    }
}
