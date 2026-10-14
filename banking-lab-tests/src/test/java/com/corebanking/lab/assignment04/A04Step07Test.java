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
class A04Step07Test extends FinancialLabSupport {
    @Test
    void concurrent_conservation() throws Exception {
        for (HttpResponse<String> r : concurrent(40, i -> () -> transfer(5))) committed(r);
        conserved(1100); reconciled();
        assertThat(scalar("SELECT count(*) FROM ledger_entries")).isEqualTo(80);
    }
}
