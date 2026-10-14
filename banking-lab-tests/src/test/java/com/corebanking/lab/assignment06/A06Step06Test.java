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
class A06Step06Test extends FinancialLabSupport {
    @Test
    void response_lost() throws Exception {
        loseResponseAfterCommit();
        HttpResponse<String> uncertain = transfer(orchestrator, source, target, 200, "lost");
        assertThat(uncertain.statusCode()).isIn(201, 503, 504);
        UUID id = committed(transfer(orchestrator, source, target, 200, "lost"));
        assertThat(balance(source)).isEqualTo(800); ledgerPair(id);
    }
}
