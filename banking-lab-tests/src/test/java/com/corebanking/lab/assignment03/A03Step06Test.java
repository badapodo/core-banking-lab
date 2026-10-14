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
class A03Step06Test extends FinancialLabSupport {
    @Test
    void immutable_history() throws Exception {
        UUID id = committed(transfer(200));
        String before = expect(request(core, "GET", "/api/v1/accounts/" + source + "/ledger", null, null), 200).get(0).toString();
        expect(request(core, "DELETE", "/api/v1/accounts/" + source + "/ledger", null, null), 405);
        committed(transfer(200));
        JsonNode entries = expect(request(core, "GET", "/api/v1/accounts/" + source + "/ledger", null, null), 200);
        assertThat(entries.findValuesAsText("transferId")).contains(id.toString());
        assertThat(entries.toString()).contains(before);
        ledgerPair(id);
    }
}
