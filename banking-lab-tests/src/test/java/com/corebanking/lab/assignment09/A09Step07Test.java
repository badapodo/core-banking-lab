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
class A09Step07Test extends FinancialLabSupport {
    @Test
    void outbox_recovery() throws Exception {
        UUID id = committed(transfer(200)); core.crash();
        sql("UPDATE outbox_events SET state='PUBLISHING', attempts=attempts+1 WHERE transfer_id=?", id);
        core.start(); expect(request(core, "POST", "/api/v1/outbox/publish", null, null), 200);
        awaitPublished(id);
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> assertThat(scalar("SELECT count(*) FROM outbox_events WHERE transfer_id=? AND state='PUBLISHED'",id)).isEqualTo(1));
    }
}
