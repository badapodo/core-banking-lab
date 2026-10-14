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
class A02Step06Test extends FinancialLabSupport {
    @Test
    void rollback() throws Exception {
        sql("ALTER TABLE accounts ADD CONSTRAINT lab_reject_credit CHECK (account_id <> '" + target + "' OR balance <> 300)");
        error(transfer(200), 503, "FINANCIAL_WRITE_FAILED");
        unchanged();
    }
}
