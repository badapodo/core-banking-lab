# Testing Guide

## Commands

```bash
./scripts/lab doctor
./gradlew :banking-lab-tests:environmentTest
./gradlew :banking-lab-tests:infrastructureTest -PlabInfrastructure=10
./scripts/lab test 02-01
./scripts/lab test 03-02
./scripts/lab verify 02
./scripts/lab verify 05
./scripts/lab verify all
./scripts/lab status
```

verify AA runs every registered Step from 01 through AA. verify all uses activeAssignment in lab.properties (provided default 12). Lower that number during incremental study. Explicit test AA-SS may address a later Step without activating all earlier test classes.
status reads real recorded outcomes without rerunning tests. NEVER_RUN means no result exists, STALE means the source fingerprint changed, PASS is a real successful test, TODO_FAIL is an explicit TODO assertion, FUNCTIONAL_FAIL is another test failure, ENVIRONMENT_ERROR is startup/container failure. Reports live in banking-lab-tests/build/lab-results; clean removes them.
JUnit XML and HTML live in banking-lab-tests/build/test-results and build/reports/tests.
An intentional TODO failure returns a nonzero process status. Scripts never convert an environment failure into a successful functional run.
Unknown steps, invalid ranges, missing registrations and zero test execution are errors.

## Initial verification

```bash
./gradlew :banking-lab-tests:compileTestJava
./gradlew :banking-lab-tests:initialFailureTest
./gradlew test
```

environmentTest must pass. initialFailureTest selects exactly AA-01 for 01..12 and intentionally fails with corresponding TODO messages. Default test does not run future failing functionality.
The first test of a later assignment may initially fail at a prerequisite transfer call; this is an explicit unimplemented feature, not an infrastructure error. After implementing the prerequisite, the test proceeds to that assignment's behavioral assertions.
Use infrastructureTest independently to prove Kafka/service/fault fixtures work before interpreting feature failures. labInfrastructure must be in 02..12 and activates the same infrastructure as the chosen assignment.

## Isolation and observable results

Existing A01 tests and contracts are preserved. Per-class JVM forks avoid reuse of stopped A01 containers. New FinancialLabSupport uses fresh PostgreSQL data and service processes for every method; there is no dependence on test order.
Funding is test-only SQL data creation with an opening baseline, never a production funding endpoint.
Concurrency uses explicit latches. Asynchronous completion uses Awaitility. Kafka duplicate tests wait for delivery evidence as well as effect count.
DB fault triggers and sequences are confined to a fresh test database. A nontransactional sequence confirms that the consumer attempted a failing write before the fault is removed.
WireMock injects connection resets, delays, upstream errors and post-commit response loss. Child Process.destroyForcibly models process termination; reopening a context does not substitute for a process restart.
The Core financial assertions use HTTP plus PostgreSQL totals/ledger/baseline state rather than calling a particular repository implementation.

## Requirements and troubleshooting

Java 21, a working Gradle bootstrap/cache or download access, Docker daemon access and reachable image/dependency registries are required.
doctor prints Java/Gradle/Docker details and executes actual PostgreSQL Testcontainers smoke tests. It does not mark Docker absence as a skipped success.
ENVIRONMENT_ERROR includes process exits and startup logs. Docker discovery, image pull, migration or port failures must be fixed before grading functionality.
The existing gradlew is a Unix bootstrap that reuses cached Gradle or downloads a distribution. It is not the standard checked-in Gradle wrapper JAR; the repository currently uses cached Gradle 8.7 while its fallback requests 8.10.2.
