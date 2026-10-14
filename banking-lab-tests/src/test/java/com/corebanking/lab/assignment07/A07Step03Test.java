package com.corebanking.lab.assignment07;

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
@Tag("assignment07")
class A07Step03Test extends FinancialLabSupport {
    @Test
    void validation_timeout() throws Exception {
        validation.resetAll(); validation.stubFor(get(urlPathMatching("/recipients/.*")).willReturn(aResponse().withFixedDelay(4000).withStatus(200)));
        UUID id = UUID.fromString(saga(200, "timeout").path("sagaId").asText());
        awaitSaga(id, "RETRY_PENDING"); unchanged();
    }
}
