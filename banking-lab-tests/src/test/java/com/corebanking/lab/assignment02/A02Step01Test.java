package com.corebanking.lab.assignment02;

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
@Tag("assignment02")
class A02Step01Test extends FinancialLabSupport {
    @Test
    void normal_transfer() throws Exception {
        UUID id = committed(transfer(200));
        JsonNode result = expect(request(core, "GET", "/api/v1/transfers/" + id, null, null), 200);
        assertThat(result.path("amount").asLong()).isEqualTo(200);
        assertThat(balance(source)).isEqualTo(800);
        assertThat(balance(target)).isEqualTo(300);
        assertThat(scalar("SELECT count(*) FROM transfers WHERE transfer_id=? AND status='COMMITTED'", id)).isEqualTo(1);
        conserved(1100);
    }
}
