package com.corebanking.lab.assignment05;

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
@Tag("assignment05")
class A05Step04Test extends FinancialLabSupport {
    @Test
    void lost_client_response() throws Exception {
        transfer(core, source, target, 200, "lost");
        UUID id = committed(transfer(core, source, target, 200, "lost"));
        assertThat(balance(source)).isEqualTo(800);
        assertThat(scalar("SELECT count(*) FROM transfers WHERE status='COMMITTED'")).isEqualTo(1);
        ledgerPair(id);
    }
}
