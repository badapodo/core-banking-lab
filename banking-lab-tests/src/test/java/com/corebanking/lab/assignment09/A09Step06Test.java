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
class A09Step06Test extends FinancialLabSupport {
    @Test
    void duplicate_publish() throws Exception {
        UUID id = committed(transfer(200));
        UUID eventId;
        try (Connection c=connection(); PreparedStatement s=c.prepareStatement("SELECT event_id FROM outbox_events WHERE transfer_id=?")) {
            s.setObject(1,id); try (ResultSet r=s.executeQuery()) { assertThat(r.next()).isTrue(); eventId=r.getObject(1,UUID.class); }
        }
        expect(request(core,"POST","/api/v1/outbox/publish",null,null),200); awaitEffect("audit",eventId,1);
        sql("UPDATE outbox_events SET state='PENDING' WHERE transfer_id=?",id);
        expect(request(core,"POST","/api/v1/outbox/publish",null,null),200); awaitEffect("audit",eventId,1);
        awaitDeliveries("audit", eventId, 2);
        assertThat(projectionCount("audit",eventId)).isEqualTo(1);
    }
}
