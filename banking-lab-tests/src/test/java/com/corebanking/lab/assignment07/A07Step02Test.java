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
class A07Step02Test extends FinancialLabSupport {
    @Test
    void recipient_rejected() throws Exception {
        validation.resetAll(); validation.stubFor(get(urlPathMatching("/recipients/.*")).willReturn(okJson("{\"valid\":false}")));
        UUID id = UUID.fromString(saga(200, "reject").path("sagaId").asText());
        awaitSaga(id, "REJECTED"); unchanged();
    }
}
