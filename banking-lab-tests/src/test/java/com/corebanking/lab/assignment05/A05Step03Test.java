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
class A05Step03Test extends FinancialLabSupport {
    @Test
    void parallel_duplicate() throws Exception {
        Set<UUID> ids = new HashSet<>();
        for (HttpResponse<String> r : concurrent(20, i -> () -> transfer(core, source, target, 200, "parallel"))) ids.add(committed(r));
        assertThat(ids).hasSize(1); assertThat(balance(source)).isEqualTo(800);
        ledgerPair(ids.iterator().next());
    }
}
