package com.corebanking.lab.assignment10;

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
@Tag("assignment10")
class A10Step03Test extends FinancialLabSupport {
    @Test
    void db_disconnect() throws Exception {
        terminateDatabaseSessions("lab"); UUID id=committed(transfer(core,source,target,200,"db"));
        assertThat(committed(transfer(core,source,target,200,"db"))).isEqualTo(id); ledgerPair(id);
    }
}
