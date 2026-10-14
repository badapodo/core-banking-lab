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
class A09Step05Test extends FinancialLabSupport {
    @Test
    void republish() throws Exception {
        UUID id = committed(transfer(200)); awaitPublished(id);
        sql("UPDATE outbox_events SET state='PENDING', published_at=NULL WHERE transfer_id=?", id);
        expect(request(core, "POST", "/api/v1/outbox/publish", null, null), 200); awaitPublicationCount(id, 2);
        assertThat(scalar("SELECT count(*) FROM outbox_events WHERE transfer_id=?", id)).isEqualTo(1);
    }
}
