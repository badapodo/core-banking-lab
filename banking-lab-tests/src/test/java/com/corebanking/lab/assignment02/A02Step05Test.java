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
class A02Step05Test extends FinancialLabSupport {
    @Test
    void invalid_amount() throws Exception {
        for (long amount : new long[]{0, -1}) error(transfer(amount), 400, "INVALID_AMOUNT");
        unchanged();
    }
}
