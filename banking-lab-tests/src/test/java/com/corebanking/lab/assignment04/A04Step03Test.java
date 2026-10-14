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
class A04Step03Test extends FinancialLabSupport {
    @Test
    void opposite_transfers() throws Exception {
        for (HttpResponse<String> r : concurrent(20, i -> () -> i % 2 == 0 ? transfer(10) : transfer(core, target, source, 10, null))) committed(r);
        assertThat(balance(source)).isEqualTo(1000); assertThat(balance(target)).isEqualTo(100);
        conserved(1100); reconciled();
    }
}
