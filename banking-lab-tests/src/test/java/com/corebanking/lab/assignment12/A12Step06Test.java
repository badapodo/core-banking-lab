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
class A12Step06Test extends FinancialLabSupport {
    @Test
    void adapter_contract() throws Exception {
        com.corebanking.contracts.workload.WorkloadGenerator generator=new com.corebanking.lab.WorkloadGeneratorSkeleton();
        var fixture=new com.corebanking.lab.support.SyntheticDatasetFixture();
        var work=generator.generate(fixture,42);
        var repeat=generator.generate(new com.corebanking.lab.support.SyntheticDatasetFixture(),42);
        assertThat(work).isEqualTo(repeat); assertThat(work.transfers()).hasSize(2);
        assertThat(work.accountMapping()).containsKeys("A","B","C");
        assertThat(work.openingBalances().values()).allMatch(value -> value >= 0);
        assertThat(work.provenance()).isEqualTo("SYNTHETIC");
    }
}
