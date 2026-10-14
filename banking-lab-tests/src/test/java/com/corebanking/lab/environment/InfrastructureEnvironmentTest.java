package com.corebanking.lab.environment;

import com.corebanking.lab.support.FinancialLabSupport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.time.Duration;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;

@Tag("infrastructure")
class InfrastructureEnvironmentTest extends FinancialLabSupport {
    @Test
    void containers_processes_and_fault_injection_are_ready() throws Exception {
        assertThat(scalar("SELECT count(*) FROM accounts")).isEqualTo(2);
        assertThat(scalar("SELECT count(*) FROM funding_baselines")).isEqualTo(2);
        assertThat(http.send(java.net.http.HttpRequest.newBuilder(java.net.URI.create(core.url()+"/lab/health")).GET().build(),
                java.net.http.HttpResponse.BodyHandlers.ofString()).statusCode()).isEqualTo(200);
        if (orchestrator != null) {
            try (var c=DriverManager.getConnection(dbUrl("orchestrator"),"orch","orch"); var s=c.createStatement(); var r=s.executeQuery("SELECT count(*) FROM sagas")) {
                r.next(); assertThat(r.getLong(1)).isZero();
            }
            assertThatThrownBy(() -> DriverManager.getConnection(dbUrl("core"),"orch","orch")).isInstanceOf(SQLException.class);
            coreFault(503);
            var injected = http.send(
                java.net.http.HttpRequest.newBuilder(java.net.URI.create(gateway.baseUrl()+"/lab/health")).GET().build(),
                java.net.http.HttpResponse.BodyHandlers.ofString());
            assertThat(injected.statusCode()).isEqualTo(503);
            coreTimeout();
            assertThatThrownBy(() -> http.send(java.net.http.HttpRequest.newBuilder(java.net.URI.create(gateway.baseUrl()+"/probe"))
                    .timeout(Duration.ofMillis(100)).GET().build(),java.net.http.HttpResponse.BodyHandlers.ofString()))
                    .isInstanceOf(java.net.http.HttpTimeoutException.class);
            coreDisconnect();
            assertThatThrownBy(() -> http.send(java.net.http.HttpRequest.newBuilder(java.net.URI.create(gateway.baseUrl()+"/probe"))
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString("{}")).build(),java.net.http.HttpResponse.BodyHandlers.ofString()))
                    .isInstanceOf(java.io.IOException.class);
            restoreCoreNetwork();
            orchestrator.restart();
        }
        if (kafka != null) {
            try(var c=DriverManager.getConnection(dbUrl("projections"),"proj","proj"); var s=c.createStatement(); var r=s.executeQuery("SELECT count(*) FROM consumed_events")) {
                r.next(); assertThat(r.getLong(1)).isZero();
            }
            String probeTopic=topic+"-probe";
            try(var admin=org.apache.kafka.clients.admin.AdminClient.create(java.util.Map.of("bootstrap.servers",kafka.getBootstrapServers()))) {
                admin.createTopics(java.util.List.of(new org.apache.kafka.clients.admin.NewTopic(probeTopic,1,(short)1))).all().get(30,java.util.concurrent.TimeUnit.SECONDS);
            }
            UUID eventId=UUID.randomUUID();
            sendEvent(probeTopic,event(eventId,UUID.randomUUID(),1));
            try(var consumer=eventObserver(probeTopic)) {
                await().atMost(Duration.ofSeconds(20)).until(() -> {
                    for(var record:consumer.poll(Duration.ofMillis(200))) {
                        if(mapper.readTree(record.value()).path("eventId").asText().equals(eventId.toString())) return true;
                    }
                    return false;
                });
            }
            projections.restart();
        }
        core.restart();
        unchanged();
    }
}
