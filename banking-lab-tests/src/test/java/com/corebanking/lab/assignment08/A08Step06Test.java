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
class A08Step06Test extends FinancialLabSupport {
    @Test
    void out_of_order() throws Exception {
        UUID id = committed(transfer(200)), earlier = UUID.randomUUID(), later = UUID.randomUUID();
        sendEvent(event(later, id, 2)); sendEvent(event(earlier, id, 1));
        awaitEffect("audit", later, 1); awaitEffect("audit", earlier, 1);
        assertThat(projection("audit", later).path("sequence").asLong()).isEqualTo(2);
        conserved(1100);
    }
}
