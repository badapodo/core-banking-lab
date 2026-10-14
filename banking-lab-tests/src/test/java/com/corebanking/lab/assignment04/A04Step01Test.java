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
class A04Step01Test extends FinancialLabSupport {
    @Test
    void parallel_withdrawals() throws Exception {
        for (HttpResponse<String> r : concurrent(20, i -> () -> transfer(10))) committed(r);
        assertThat(balance(source)).isEqualTo(800);
        assertThat(balance(target)).isEqualTo(300);
        conserved(1100); reconciled();
    }
}
