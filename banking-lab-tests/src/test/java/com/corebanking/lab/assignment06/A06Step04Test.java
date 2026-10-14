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
class A06Step04Test extends FinancialLabSupport {
    @Test
    void connection_failure() throws Exception {
        coreDisconnect();
        error(transfer(orchestrator, source, target, 200, "disconnect"), 503, "UPSTREAM_UNAVAILABLE"); unchanged();
    }
}
