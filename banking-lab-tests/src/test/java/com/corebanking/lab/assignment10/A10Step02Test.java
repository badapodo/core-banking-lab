package com.corebanking.lab.assignment10;

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
@Tag("assignment10")
class A10Step02Test extends FinancialLabSupport {
    @Test
    void consumer_restart() throws Exception {
        projections.crash(); UUID id=committed(transfer(200)), eventId=outboxEventId(id);
        projections.start(); awaitEffect("audit",eventId,1); conserved(1100);
    }
}
