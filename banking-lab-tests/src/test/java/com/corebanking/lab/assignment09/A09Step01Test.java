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
class A09Step01Test extends FinancialLabSupport {
    @Test
    void atomic_commit() throws Exception {
        UUID id = committed(transfer(200));
        assertThat(scalar("SELECT count(*) FROM outbox_events WHERE transfer_id=?", id)).isEqualTo(1); ledgerPair(id);
    }
}
