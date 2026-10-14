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
class A12Step02Test extends FinancialLabSupport {
    @Test
    void reproducible_mapping() throws Exception {
        Map<String,Object> spec=Map.of("seed",42,"accounts",8,"requests",20,"concurrency",2,"initialBalance",1000,"dataset","synthetic");
        JsonNode first=expect(request(core,"POST","/api/v1/workloads",spec,null),200);
        JsonNode second=expect(request(core,"POST","/api/v1/workloads",spec,null),200);
        assertThat(second.path("accountMapping")).isEqualTo(first.path("accountMapping"));
    }
}
