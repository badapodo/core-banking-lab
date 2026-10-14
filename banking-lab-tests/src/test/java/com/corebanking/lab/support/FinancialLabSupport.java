package com.corebanking.lab.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.ResponseTransformerV2;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.net.URI;
import java.net.ServerSocket;
import java.net.http.*;
import java.nio.file.*;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntFunction;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/** Per-test databases, real service processes, and externally injected faults. */
public abstract class FinancialLabSupport {
    protected final ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    protected final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    protected PostgreSQLContainer<?> postgres;
    protected KafkaContainer kafka;
    protected WireMockServer gateway, validation;
    protected ServiceProcess core, orchestrator, projections;
    protected UUID source, target;
    protected String topic;
    protected String projectionTopic;
    protected int assignment;
    protected Path logs;
    private final AtomicBoolean loseCommittedResponse = new AtomicBoolean();
    private final AtomicBoolean holdCommittedResponse = new AtomicBoolean();
    private final CountDownLatch committedSignal = new CountDownLatch(1);
    private final CountDownLatch responseRelease = new CountDownLatch(1);

    @BeforeEach
    void startLab() throws Exception {
        assignment = getClass().getSimpleName().startsWith("A")
                ? Integer.parseInt(getClass().getSimpleName().substring(1, 3))
                : Integer.getInteger("lab.infrastructureAssignment", 10);
        logs = Files.createTempDirectory("banking-lab-" + assignment + "-");
        try {
            postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("core").withUsername("lab").withPassword("lab");
            postgres.start();
            sql("CREATE ROLE orch LOGIN PASSWORD 'orch'");
            sql("CREATE ROLE proj LOGIN PASSWORD 'proj'");
            sql("CREATE DATABASE orchestrator OWNER orch");
            sql("CREATE DATABASE projections OWNER proj");
            sql("REVOKE CONNECT ON DATABASE core FROM PUBLIC");
            if (assignment >= 8 && assignment <= 11) {
                kafka = new KafkaContainer("apache/kafka-native:3.8.0");
                kafka.start();
                topic = "transfers-" + UUID.randomUUID();
                boolean isolatedConsumerFixture = assignment == 8 && !getClass().getSimpleName().contains("Step01");
                projectionTopic = isolatedConsumerFixture ? topic + "-fixture" : topic;
                try (AdminClient admin = AdminClient.create(Map.of("bootstrap.servers", kafka.getBootstrapServers()))) {
                    admin.createTopics(List.of(new NewTopic(topic, 1, (short) 1))).all().get(30, TimeUnit.SECONDS);
                    if (isolatedConsumerFixture) admin.createTopics(List.of(new NewTopic(projectionTopic, 1, (short) 1))).all().get(30, TimeUnit.SECONDS);
                }
            }
            core = new ServiceProcess("com.corebanking.BankingApplication", "core", "lab", "lab",
                    "classpath:db/migration,classpath:db/lab-core", true);
            core.start();
            source = fund(1000);
            target = fund(100);
            if ((assignment >= 6 && assignment <= 7) || assignment == 10) startOrchestrator();
            if (assignment >= 8 && assignment <= 11) startProjections();
        } catch (Throwable error) {
            stopLab();
            throw new IllegalStateException("ENVIRONMENT_ERROR: lab infrastructure did not start; logs: " + logs, error);
        }
    }

    protected void startOrchestrator() throws Exception {
        gateway = new WireMockServer(com.github.tomakehurst.wiremock.core.WireMockConfiguration.options()
                .dynamicPort().extensions(new ResponseTransformerV2() {
                    public String getName() { return "lose-committed-response"; }
                    public Response transform(Response response, ServeEvent event) {
                        if (response.getStatus() == 201 && holdCommittedResponse.get()) {
                            committedSignal.countDown();
                            try { responseRelease.await(10, TimeUnit.SECONDS); }
                            catch (InterruptedException error) { Thread.currentThread().interrupt(); }
                        }
                        if (response.getStatus() == 201 && loseCommittedResponse.compareAndSet(true, false)) {
                            return Response.response().fault(Fault.EMPTY_RESPONSE).build();
                        }
                        return response;
                    }
                }));
        validation = new WireMockServer(com.github.tomakehurst.wiremock.core.WireMockConfiguration.options().dynamicPort());
        gateway.start();
        validation.start();
        restoreCoreNetwork();
        validation.stubFor(get(urlPathMatching("/recipients/.*")).willReturn(okJson("{\"valid\":true}")));
        validation.stubFor(post(urlEqualTo("/followups")).willReturn(ok()));
        orchestrator = new ServiceProcess("com.banklab.orchestrator.OrchestratorApplication",
                "orchestrator", "orch", "orch", "classpath:db/orchestrator", false);
        orchestrator.start();
    }

    protected void startProjections() throws Exception {
        projections = new ServiceProcess("com.banklab.projections.ProjectionApplication",
                "projections", "proj", "proj", "classpath:db/projections", false);
        projections.start();
    }

    @AfterEach
    void stopLab() {
        responseRelease.countDown();
        if (projections != null) projections.close();
        if (orchestrator != null) orchestrator.close();
        if (core != null) core.close();
        if (gateway != null) gateway.stop();
        if (validation != null) validation.stop();
        if (kafka != null) kafka.stop();
        if (postgres != null) postgres.stop();
    }

    protected Connection connection() throws SQLException {
        return DriverManager.getConnection(postgres.getJdbcUrl(), "lab", "lab");
    }
    protected void sql(String text, Object... values) throws SQLException {
        try (Connection c = connection(); PreparedStatement s = c.prepareStatement(text)) {
            for (int i = 0; i < values.length; i++) s.setObject(i + 1, values[i]);
            s.execute();
        }
    }
    protected long scalar(String text, Object... values) throws SQLException {
        try (Connection c = connection(); PreparedStatement s = c.prepareStatement(text)) {
            for (int i = 0; i < values.length; i++) s.setObject(i + 1, values[i]);
            try (ResultSet r = s.executeQuery()) { r.next(); return r.getLong(1); }
        }
    }
    protected UUID fund(long amount) throws SQLException {
        UUID id = UUID.randomUUID();
        try (Connection c = connection()) {
            c.setAutoCommit(false);
            try (PreparedStatement account = c.prepareStatement(
                    "INSERT INTO accounts(account_id, balance, status) VALUES (?, ?, 'ACTIVE')");
                 PreparedStatement baseline = c.prepareStatement(
                    "INSERT INTO funding_baselines(account_id, opening_balance) VALUES (?, ?)")) {
                account.setObject(1, id); account.setLong(2, amount); account.executeUpdate();
                baseline.setObject(1, id); baseline.setLong(2, amount); baseline.executeUpdate();
            }
            c.commit();
        }
        return id;
    }
    protected long balance(UUID id) throws SQLException {
        return scalar("SELECT balance FROM accounts WHERE account_id = ?", id);
    }
    protected long total() throws SQLException { return scalar("SELECT sum(balance) FROM accounts"); }
    protected void conserved(long expected) throws SQLException {
        assertThat(total()).isEqualTo(expected);
        assertThat(scalar("SELECT count(*) FROM accounts WHERE balance < 0")).isZero();
    }
    protected void unchanged() throws SQLException {
        assertThat(balance(source)).isEqualTo(1000);
        assertThat(balance(target)).isEqualTo(100);
        assertThat(scalar("SELECT count(*) FROM transfers WHERE status = 'COMMITTED'")).isZero();
        assertThat(scalar("SELECT count(*) FROM ledger_entries")).isZero();
    }
    protected void reconciled() throws SQLException {
        assertThat(scalar("""
                SELECT count(*) FROM accounts a JOIN funding_baselines f USING(account_id)
                WHERE a.balance <> f.opening_balance + COALESCE(
                    (SELECT sum(CASE WHEN side = 'CREDIT' THEN amount ELSE -amount END)
                     FROM ledger_entries l WHERE l.account_id = a.account_id), 0)
                """)).isZero();
        assertThat(scalar("""
                SELECT count(*) FROM (
                  SELECT transfer_id FROM ledger_entries GROUP BY transfer_id
                  HAVING sum(CASE WHEN side = 'DEBIT' THEN amount ELSE -amount END) <> 0
                ) bad
                """)).isZero();
    }
    protected Map<String, Object> command(UUID from, UUID to, long amount) {
        return Map.of("sourceAccountId", from, "targetAccountId", to, "amount", amount);
    }
    protected HttpResponse<String> request(ServiceProcess service, String method, String path, Object body, String key) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(service.url() + path))
                .timeout(Duration.ofSeconds(8)).header("Content-Type", "application/json");
        if (key != null) builder.header("Idempotency-Key", key);
        builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }
    protected HttpResponse<String> transfer(ServiceProcess service, UUID from, UUID to, long amount, String key) throws Exception {
        return request(service, "POST", "/api/v1/transfers", command(from, to, amount), key);
    }
    protected HttpResponse<String> transfer(long amount) throws Exception { return transfer(core, source, target, amount, null); }
    protected JsonNode expect(HttpResponse<String> response, int status) throws Exception {
        assertThat(response.statusCode()).as("HTTP contract; body: %s", response.body()).isEqualTo(status);
        return response.body().isBlank() ? mapper.createObjectNode() : mapper.readTree(response.body());
    }
    protected void error(HttpResponse<String> response, int status, String code) throws Exception {
        assertThat(expect(response, status).path("code").asText()).isEqualTo(code);
    }
    protected UUID committed(HttpResponse<String> response) throws Exception {
        JsonNode json = expect(response, 201);
        assertThat(json.path("status").asText()).isEqualTo("COMMITTED");
        return UUID.fromString(json.path("transferId").asText());
    }
    protected void ledgerPair(UUID id) throws SQLException {
        assertThat(scalar("SELECT count(*) FROM ledger_entries WHERE transfer_id = ?", id)).isEqualTo(2);
        assertThat(scalar("SELECT sum(amount) FROM ledger_entries WHERE transfer_id = ? AND side = 'DEBIT'", id)).isEqualTo(200);
        assertThat(scalar("SELECT sum(amount) FROM ledger_entries WHERE transfer_id = ? AND side = 'CREDIT'", id)).isEqualTo(200);
        reconciled();
    }
    protected void rejectWrites(String table) throws SQLException {
        if (!Set.of("ledger_entries", "idempotency_records", "outbox_events").contains(table)) throw new IllegalArgumentException(table);
        sql("CREATE FUNCTION lab_reject_write() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'lab injected failure'; END $$");
        sql("CREATE TRIGGER lab_failure BEFORE INSERT ON " + table + " FOR EACH ROW EXECUTE FUNCTION lab_reject_write()");
    }
    protected List<HttpResponse<String>> concurrent(int count, IntFunction<Callable<HttpResponse<String>>> jobs) throws Exception {
        CountDownLatch ready = new CountDownLatch(count), start = new CountDownLatch(1);
        try (ExecutorService pool = Executors.newFixedThreadPool(count)) {
            List<Future<HttpResponse<String>>> futures = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                Callable<HttpResponse<String>> job = jobs.apply(i);
                futures.add(pool.submit(() -> {
                    ready.countDown();
                    if (!start.await(10, TimeUnit.SECONDS)) throw new AssertionError("start barrier timed out");
                    return job.call();
                }));
            }
            assertThat(ready.await(10, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            List<HttpResponse<String>> results = new ArrayList<>();
            for (Future<HttpResponse<String>> future : futures) results.add(future.get(30, TimeUnit.SECONDS));
            return results;
        } finally { start.countDown(); }
    }
    protected void restoreCoreNetwork() {
        gateway.resetAll();
        gateway.stubFor(any(anyUrl()).willReturn(aResponse().proxiedFrom(core.url())));
    }
    protected void coreFault(int status) {
        gateway.resetAll();
        gateway.stubFor(any(anyUrl()).willReturn(aResponse().withStatus(status)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"code\":\"UPSTREAM_UNAVAILABLE\",\"message\":\"injected\"}")));
    }
    protected void coreTimeout() {
        gateway.resetAll();
        gateway.stubFor(any(anyUrl()).willReturn(aResponse().withFixedDelay(4000).withStatus(503)));
    }
    protected void coreDisconnect() {
        gateway.resetAll();
        gateway.stubFor(any(anyUrl()).willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));
    }
    protected void loseResponseAfterCommit() { loseCommittedResponse.set(true); }
    protected void holdResponseAfterCommit() { holdCommittedResponse.set(true); }
    protected void awaitCommittedResponse() throws InterruptedException {
        assertThat(committedSignal.await(15, TimeUnit.SECONDS)).as("Core committed through HTTP proxy").isTrue();
    }
    protected void releaseResponse() { holdCommittedResponse.set(false); responseRelease.countDown(); }
    protected JsonNode saga(long amount, String key) throws Exception {
        return expect(request(orchestrator, "POST", "/api/v1/sagas", command(source, target, amount), key), 202);
    }
    protected JsonNode sagaState(UUID id) throws Exception {
        return expect(request(orchestrator, "GET", "/api/v1/sagas/" + id, null, null), 200);
    }
    protected void awaitSaga(UUID id, String state) {
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> assertThat(sagaState(id).path("state").asText()).isEqualTo(state));
    }
    protected Map<String, Object> event(UUID id, UUID transfer, long sequence) {
        return Map.of("eventId", id, "schemaVersion", 1, "transferId", transfer,
                "sourceAccountId", source, "targetAccountId", target, "amount", 200,
                "sequence", sequence, "occurredAt", Instant.now().toString());
    }
    protected void sendEvent(Map<String, Object> event) throws Exception {
        sendEvent(projectionTopic, event);
    }
    protected void sendEvent(String destination, Map<String, Object> event) throws Exception {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", kafka.getBootstrapServers());
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        properties.put("acks", "all");
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            producer.send(new ProducerRecord<>(destination, event.get("transferId").toString(),
                    mapper.writeValueAsString(event))).get(15, TimeUnit.SECONDS);
        }
    }
    protected JsonNode projection(String consumer, UUID event) throws Exception {
        return expect(request(projections, "GET", "/api/v1/consumers/" + consumer + "/events/" + event, null, null), 200);
    }
    protected void awaitEffect(String consumer, UUID event, int count) {
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
                assertThat(projection(consumer, event).path("effectCount").asInt()).isEqualTo(count));
    }
    protected void awaitDeliveries(String consumer, UUID event, int count) {
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
                assertThat(projection(consumer, event).path("deliveryCount").asInt()).isGreaterThanOrEqualTo(count));
    }
    protected KafkaConsumer<String, String> eventObserver() {
        return eventObserver(topic);
    }
    protected KafkaConsumer<String, String> eventObserver(String destination) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", kafka.getBootstrapServers());
        properties.put("group.id", "observer-" + UUID.randomUUID());
        properties.put("auto.offset.reset", "earliest");
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(List.of(destination));
        return consumer;
    }
    protected void awaitPublished(UUID transfer) throws Exception {
        try (KafkaConsumer<String, String> consumer = eventObserver()) {
            await().atMost(Duration.ofSeconds(20)).until(() -> {
                for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(200))) {
                    if (mapper.readTree(record.value()).path("transferId").asText().equals(transfer.toString())) return true;
                }
                return false;
            });
        }
    }
    protected void awaitPublicationCount(UUID transfer, int expected) throws Exception {
        try (KafkaConsumer<String, String> consumer = eventObserver()) {
            java.util.concurrent.atomic.AtomicInteger count = new java.util.concurrent.atomic.AtomicInteger();
            Set<String> eventIds = new HashSet<>();
            await().atMost(Duration.ofSeconds(20)).until(() -> {
                for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(200))) {
                    JsonNode event = mapper.readTree(record.value());
                    if (event.path("transferId").asText().equals(transfer.toString())) {
                        count.incrementAndGet(); eventIds.add(event.path("eventId").asText());
                    }
                }
                return count.get() >= expected;
            });
            assertThat(eventIds).as("republication preserves event identity").hasSize(1);
        }
    }
    protected UUID outboxEventId(UUID transfer) throws SQLException {
        try (Connection c=connection(); PreparedStatement s=c.prepareStatement("SELECT event_id FROM outbox_events WHERE transfer_id=?")) {
            s.setObject(1,transfer);
            try (ResultSet r=s.executeQuery()) { assertThat(r.next()).isTrue(); return r.getObject(1,UUID.class); }
        }
    }
    protected void awaitConsumerOffset(String consumer) throws Exception {
        try (AdminClient admin=AdminClient.create(Map.of("bootstrap.servers",kafka.getBootstrapServers()))) {
            var partition=new org.apache.kafka.common.TopicPartition(topic,0);
            await().atMost(Duration.ofSeconds(15)).until(() -> {
                long end=admin.listOffsets(Map.of(partition,org.apache.kafka.clients.admin.OffsetSpec.latest())).all().get(5,TimeUnit.SECONDS).get(partition).offset();
                var consumed=admin.listConsumerGroupOffsets(consumer+"-"+topic).partitionsToOffsetAndMetadata().get(5,TimeUnit.SECONDS).get(partition);
                return consumed != null && consumed.offset() >= end;
            });
        }
    }
    protected void pauseKafka(boolean pause) {
        if (pause) kafka.getDockerClient().pauseContainerCmd(kafka.getContainerId()).exec();
        else kafka.getDockerClient().unpauseContainerCmd(kafka.getContainerId()).exec();
    }
    protected void projectionSql(String text, Object... values) throws SQLException {
        try (Connection c = DriverManager.getConnection(dbUrl("projections"), "proj", "proj");
             PreparedStatement s = c.prepareStatement(text)) {
            for (int i = 0; i < values.length; i++) s.setObject(i + 1, values[i]);
            s.execute();
        }
    }
    protected long projectionCount(String consumer, UUID id) throws SQLException {
        try (Connection c = DriverManager.getConnection(dbUrl("projections"), "proj", "proj");
             PreparedStatement s = c.prepareStatement("SELECT COALESCE(sum(effect_count),0) FROM consumed_events WHERE consumer=? AND event_id=?")) {
            s.setString(1, consumer); s.setObject(2, id);
            try (ResultSet r = s.executeQuery()) { r.next(); return r.getLong(1); }
        }
    }
    protected String dbUrl(String database) {
        return "jdbc:postgresql://" + postgres.getHost() + ":" + postgres.getMappedPort(5432) + "/" + database;
    }
    protected void terminateDatabaseSessions(String username) throws SQLException {
        sql("SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE usename = ? AND pid <> pg_backend_pid()", username);
    }

    protected final class ServiceProcess implements AutoCloseable {
        private final String main, database, username, password, migrations;
        private final boolean coreService;
        private int port;
        private Process process;
        private Path log;
        ServiceProcess(String main, String database, String username, String password, String migrations, boolean coreService) {
            this.main = main; this.database = database; this.username = username; this.password = password;
            this.migrations = migrations; this.coreService = coreService;
        }
        public String url() { return "http://localhost:" + port; }
        public void start() throws Exception {
            try (ServerSocket socket = new ServerSocket(0)) { port = socket.getLocalPort(); }
            log = logs.resolve(database + "-" + UUID.randomUUID() + ".log");
            List<String> args = new ArrayList<>(List.of(
                    Path.of(System.getProperty("java.home"), "bin", "java").toString(), "-Xmx192m",
                    "-cp", System.getProperty("lab.classpath." + database), main,
                    "--server.port=" + port, "--spring.datasource.url=" + dbUrl(database),
                    "--spring.datasource.username=" + username, "--spring.datasource.password=" + password,
                    "--spring.flyway.locations=" + migrations, "--spring.jmx.enabled=false",
                    "--logging.level.root=WARN", "--spring.main.banner-mode=off",
                    "--lab.assignment=" + String.format("%02d", assignment),
                    "--lab.events-topic=" + (database.equals("projections") ? projectionTopic : topic),
                    "--lab.core-url=" + (gateway == null ? (core == null ? url() : core.url()) : gateway.baseUrl()),
                    "--lab.validation-url=" + (validation == null ? "" : validation.baseUrl()),
                    "--lab.http-timeout-millis=1000", "--lab.retry-limit=3",
                    "--spring.kafka.consumer.auto-offset-reset=earliest",
                    "--spring.kafka.listener.missing-topics-fatal=false",
                    "--lab.audit-group=audit-" + topic, "--lab.notification-group=notifications-" + topic));
            if (coreService) args.add("--spring.profiles.active=lab-core");
            if (kafka != null) args.add("--spring.kafka.bootstrap-servers=" + kafka.getBootstrapServers());
            else args.add("--spring.kafka.listener.auto-startup=false");
            process = new ProcessBuilder(args).redirectErrorStream(true).redirectOutput(log.toFile()).start();
            await().atMost(Duration.ofSeconds(45)).until(() -> {
                if (!process.isAlive()) throw new IllegalStateException("process exited: " + Files.readString(log));
                try {
                    return http.send(HttpRequest.newBuilder(URI.create(url() + "/lab/health"))
                            .timeout(Duration.ofSeconds(1)).GET().build(), HttpResponse.BodyHandlers.discarding()).statusCode() == 200;
                } catch (Exception ignored) { return false; }
            });
        }
        public void crash() throws Exception {
            if (process != null) {
                process.destroyForcibly();
                assertThat(process.waitFor(10, TimeUnit.SECONDS)).isTrue();
            }
        }
        public void restart() throws Exception { crash(); start(); }
        public void close() {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
                try { process.waitFor(10, TimeUnit.SECONDS); } catch (InterruptedException error) { Thread.currentThread().interrupt(); }
            }
        }
    }
}
