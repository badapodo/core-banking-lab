# Architecture

## Modules

```text
banking-domain                 (JDK-only contracts)
banking-application       ->   banking-domain, Spring context
banking-infrastructure    ->   banking-application, banking-domain, JPA/JDBC/Flyway/Kafka
banking-api               ->   banking-application, banking-domain
                          runtime: banking-infrastructure
transfer-orchestrator     ->   banking-domain, Web/JDBC/Flyway (own DB)
banking-projections       ->   banking-domain, Web/JDBC/Flyway/Kafka (own DB)
banking-lab-tests         ->   API, application, domain, orchestrator, projections, test infrastructure
```

Core Banking owns accounts, transfer confirmations, ledger, idempotency records and outbox. Balance and ledger changes share one transaction in one PostgreSQL database even when services are separated.
Transfer Orchestrator owns Saga/workflow state and has no financial mutation authority. Its compile/runtime production module does not depend on Core implementation modules.
Audit/notifications own derivative consumption state. Independent Kafka groups isolate their processing.
Reconciliation accesses Core through a read-only HTTP snapshot and replay requests; the confirmed ledger remains the financial truth source.
Confirmed transactions are not deleted or reversed for notification failure. A real cancellation requires a separate explicitly authorized reversal transaction; this lab does not implement reversal algorithms.

## Activation

| Assignment | Started infrastructure |
| --- | --- |
| 01 | Original PostgreSQL container and Spring test context |
| 02~05 | PostgreSQL + Core Java process |
| 06~07 | PostgreSQL + Core/Orchestrator processes + WireMock validation/followup |
| 08~09 | PostgreSQL + Kafka + Core/projection processes |
| 10 | PostgreSQL + Kafka + all three processes + fault proxy |
| 11 | PostgreSQL + Kafka + Core/projections and HTTP evidence contract |
| 12 | PostgreSQL + Core with workload contracts |

A02+ uses lab-core profile and an explicit Flyway location db/lab-core in addition to A01 db/migration. Default A01 startup does not enable future HTTP controllers or future migrations.
Each new test creates one PostgreSQL container and independent databases/roles for relevant services. orch/proj roles cannot connect to Core DB. Child services have each module's runtime classpath and independent heap, port and process lifetime.
Per-test data/container isolation and per-class Gradle forks preserve A01 sources while avoiding shared Spring context/container reuse and data leakage.
Kafka has a unique topic and group IDs per test. The test code stops every child process/container in teardown or startup failure. Child logs remain under the printed /tmp/banking-lab-* directory for diagnosis.

## Standalone services

서비스별 application.yml은 자체 Flyway 경로와 기본 포트(Core 8080, Orchestrator 8081, Projections 8082)를 제공한다. 각 프로세스에 SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD를 각자 소유한 DB/역할로 설정한다. Core 확장 API는 SPRING_PROFILES_ACTIVE=lab-core로 활성화한다. 분리 서비스의 LAB_CORE_URL은 Core 주소, LAB_VALIDATION_URL은 검증/후속 서비스 주소, SPRING_KAFKA_BOOTSTRAP_SERVERS는 브로커 주소로 설정한다. Testcontainers 테스트는 이를 자동 구성한다.

```bash
./gradlew :banking-api:bootRun
./gradlew :transfer-orchestrator:bootRun
./gradlew :banking-projections:bootRun
```

## Test task choice

Registered Step classes retain the A01 convention rather than splitting source sets. All future tests compile, but only explicit assignmentTest/regressionTest tasks run them.
lab-steps.csv is the authoritative step-to-class list; Gradle validates uniqueness, missing sources and unregistered Step classes. Filters fail when no matches are found, and root suite checks reject zero executed tests.
lab.properties activeAssignment controls verify all/status; explicit verify AA always selects 01..AA.
No scheduler invokes TODO business methods during Core/Orchestrator startup. Kafka listeners expose the consumer TODO only on delivery.
