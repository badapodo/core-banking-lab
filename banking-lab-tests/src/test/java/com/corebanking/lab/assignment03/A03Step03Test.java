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
class A03Step03Test extends FinancialLabSupport {
    @Test
    void ledger_failure_rollback() throws Exception {
        rejectWrites("ledger_entries");
        error(transfer(200), 503, "FINANCIAL_WRITE_FAILED");
        unchanged();
    }
}
