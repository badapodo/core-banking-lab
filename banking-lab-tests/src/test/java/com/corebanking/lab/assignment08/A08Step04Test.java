package com.corebanking.lab.assignment08;

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
@Tag("assignment08")
class A08Step04Test extends FinancialLabSupport {
    @Test
    void consumer_failure() throws Exception {
        UUID id = committed(transfer(200)), eventId = UUID.randomUUID();
        projectionSql("CREATE SEQUENCE lab_failure_attempts");
        projectionSql("CREATE FUNCTION lab_reject_projection() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN PERFORM nextval('lab_failure_attempts'); RAISE EXCEPTION 'injected'; END $$");
        projectionSql("CREATE TRIGGER lab_failure BEFORE INSERT ON consumed_events FOR EACH ROW EXECUTE FUNCTION lab_reject_projection()");
        sendEvent(event(eventId, id, 1));
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            try (Connection c=DriverManager.getConnection(dbUrl("projections"),"proj","proj"); Statement s=c.createStatement(); ResultSet r=s.executeQuery("SELECT is_called FROM lab_failure_attempts")) {
                r.next(); assertThat(r.getBoolean(1)).isTrue();
            }
        });
        assertThat(projectionCount("audit", eventId)).isZero();
        projectionSql("DROP TRIGGER lab_failure ON consumed_events");
        sendEvent(event(eventId, id, 1)); awaitEffect("audit", eventId, 1);
    }
}
