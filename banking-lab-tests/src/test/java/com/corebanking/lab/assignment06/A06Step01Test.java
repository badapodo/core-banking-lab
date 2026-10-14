package com.corebanking.lab.assignment06;

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
@Tag("assignment06")
class A06Step01Test extends FinancialLabSupport {
    @Test
    void public_contract() throws Exception {
        UUID id = committed(transfer(orchestrator, source, target, 200, "boundary"));
        assertThat(expect(request(orchestrator, "GET", "/api/v1/transfers/" + id, null, null), 200).path("transferId").asText()).isEqualTo(id.toString());
        assertThat(balance(source)).isEqualTo(800); ledgerPair(id);
    }
}
