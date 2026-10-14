package com.corebanking.lab.assignment12;

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
@Tag("assignment12")
class A12Step04Test extends FinancialLabSupport {
    @Test
    void financial_invariants() throws Exception {
        long before=total();
        JsonNode r=expect(request(core,"POST","/api/v1/workloads",Map.of("seed",9,"accounts",8,"requests",100,"concurrency",8,"initialBalance",1000,"dataset","synthetic"),null),200);
        for(String field:List.of("idempotencyViolations","conservationViolations","ledgerMismatches")) assertThat(r.path(field).asInt()).isZero();
        conserved(before+8000); reconciled();
    }
}
