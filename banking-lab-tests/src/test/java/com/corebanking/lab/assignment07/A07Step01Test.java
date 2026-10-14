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
class A07Step01Test extends FinancialLabSupport {
    @Test
    void normal_workflow() throws Exception {
        UUID id = UUID.fromString(saga(200, "normal").path("sagaId").asText());
        awaitSaga(id, "COMPLETED"); assertThat(balance(source)).isEqualTo(800); conserved(1100); reconciled();
        List<String> states=new ArrayList<>(); sagaState(id).path("stateHistory").forEach(s -> states.add(s.asText()));
        assertThat(states).containsSubsequence("RECEIVED","VALIDATING","CORE_PENDING","COMMITTED","COMPLETED");
    }
}
