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
class A05Step02Test extends FinancialLabSupport {
    @Test
    void conflicting_key() throws Exception {
        UUID id = committed(transfer(core, source, target, 200, "conflict"));
        error(transfer(core, source, target, 201, "conflict"), 409, "IDEMPOTENCY_CONFLICT");
        assertThat(balance(source)).isEqualTo(800); ledgerPair(id);
    }
}
