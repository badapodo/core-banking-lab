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
class A12Step05Test extends FinancialLabSupport {
    @Test
    void unknown_dataset() throws Exception {
        error(request(core,"POST","/api/v1/workloads",Map.of("seed",1,"accounts",8,"requests",10,"concurrency",1,"initialBalance",1000,"dataset","unavailable-real-data"),null),400,"DATASET_UNAVAILABLE");
        unchanged();
    }
}
