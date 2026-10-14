package com.corebanking.lab.assignment09;

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
@Tag("assignment09")
class A09Step02Test extends FinancialLabSupport {
    @Test
    void rollback_no_event() throws Exception {
        rejectWrites("outbox_events"); error(transfer(200), 503, "FINANCIAL_WRITE_FAILED");
        unchanged(); assertThat(scalar("SELECT count(*) FROM outbox_events")).isZero();
    }
}
