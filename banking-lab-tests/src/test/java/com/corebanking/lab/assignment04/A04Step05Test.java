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
class A04Step05Test extends FinancialLabSupport {
    @Test
    void lost_update() throws Exception {
        for (HttpResponse<String> r : concurrent(50, i -> () -> transfer(1))) committed(r);
        assertThat(balance(source)).isEqualTo(950); assertThat(balance(target)).isEqualTo(150);
        assertThat(scalar("SELECT count(*) FROM transfers WHERE status='COMMITTED'")).isEqualTo(50);
        conserved(1100); reconciled();
    }
}
