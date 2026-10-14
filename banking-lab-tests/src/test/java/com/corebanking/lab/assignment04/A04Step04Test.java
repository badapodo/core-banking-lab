package com.corebanking.lab.assignment04;

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
@Tag("assignment04")
class A04Step04Test extends FinancialLabSupport {
    @Test
    void many_accounts() throws Exception {
        List<UUID> ids = new ArrayList<>();
        for (int i = 0; i < 8; i++) ids.add(fund(1000));
        for (HttpResponse<String> r : concurrent(32, i -> () -> transfer(core, ids.get(i % 8), ids.get((i + 1) % 8), 10, null))) committed(r);
        for (UUID id : ids) assertThat(balance(id)).isEqualTo(1000);
        conserved(9100); reconciled();
    }
}
