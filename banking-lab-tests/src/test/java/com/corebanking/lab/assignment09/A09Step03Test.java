package com.corebanking.lab.assignment09;

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
@Tag("assignment09")
class A09Step03Test extends FinancialLabSupport {
    @Test
    void crash_after_commit() throws Exception {
        pauseKafka(true);
        UUID id;
        try { id = committed(transfer(200)); core.crash(); }
        finally { pauseKafka(false); }
        core.start(); expect(request(core, "POST", "/api/v1/outbox/publish", null, null), 200);
        awaitPublished(id); ledgerPair(id);
    }
}
