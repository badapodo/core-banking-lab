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
class A05Step01Test extends FinancialLabSupport {
    @Test
    void same_key() throws Exception {
        HttpResponse<String> first = transfer(core, source, target, 200, "same");
        UUID id = committed(first);
        HttpResponse<String> second = transfer(core, source, target, 200, "same");
        assertThat(committed(second)).isEqualTo(id); assertThat(second.body()).isEqualTo(first.body());
        assertThat(balance(source)).isEqualTo(800); ledgerPair(id);
    }
}
