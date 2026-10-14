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
class A08Step02Test extends FinancialLabSupport {
    @Test
    void normal_consumption() throws Exception {
        UUID id = committed(transfer(200)), eventId = UUID.randomUUID();
        sendEvent(event(eventId, id, 1)); awaitEffect("audit", eventId, 1);
        assertThat(projectionCount("audit", eventId)).isEqualTo(1); conserved(1100);
    }
}
