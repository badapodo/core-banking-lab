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
class A07Step04Test extends FinancialLabSupport {
    @Test
    void core_failure() throws Exception {
        coreFault(503);
        UUID id = UUID.fromString(saga(200, "failure").path("sagaId").asText());
        awaitSaga(id, "RETRY_PENDING"); unchanged();
    }
}
