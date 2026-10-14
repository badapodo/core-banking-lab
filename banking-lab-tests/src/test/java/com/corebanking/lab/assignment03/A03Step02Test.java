package com.corebanking.lab.assignment03;

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
@Tag("assignment03")
class A03Step02Test extends FinancialLabSupport {
    @Test
    void balanced_entries() throws Exception {
        for (int i = 0; i < 3; i++) ledgerPair(committed(transfer(200)));
        assertThat(scalar("SELECT count(*) FROM ledger_entries")).isEqualTo(6);
    }
}
