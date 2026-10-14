package com.corebanking.lab.assignment06;

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
@Tag("assignment06")
class A06Step08Test extends FinancialLabSupport {
    @Test
    void data_ownership() throws Exception {
        com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses().that().resideInAPackage("com.banklab.orchestrator..")
            .should().dependOnClassesThat().resideInAnyPackage("com.corebanking.account..", "com.corebanking.lab..", "org.springframework.data..", "jakarta.persistence..")
            .check(new com.tngtech.archunit.core.importer.ClassFileImporter().importPackages("com.banklab.orchestrator"));
        assertThatThrownBy(() -> DriverManager.getConnection(dbUrl("core"), "orch", "orch")).isInstanceOf(SQLException.class);
    }
}
