# Expansion Verification

검증일: 2026-10-14 (Asia/Seoul). 이 결과물은 정답 금융 시스템이 아니라 테스트 기반 실습 환경이다.

## 기존 프로젝트 분석

작업 전 README.md, AGENTS.md, settings.gradle, 각 모듈 build.gradle, A01 계약/스켈레톤/테스트/스크립트를 확인했다.
기존 구조는 Java 21 / Spring Boot 3.3.5의 5개 Gradle 모듈이다. domain → application → infrastructure/API 방향이며 A01 AccountServiceImpl는 TODO 상태였다.
기존 environmentTest 2개는 통과했고 01-01은 201 대신 501 {"code":"TODO","message":"TODO: A01"}로 실패했다.
기존 lab은 01만 실행 가능했고 verify는 전체 assignment 태그, status는 테스트를 다시 실행하는 방식이었다.
A01 Account API, DTO, 서비스, 저장소 포트, 원래 5개 테스트, PostgreSQLContainerSupport, V1 migration, Assignment 문서는 수정하지 않았다. 공통 오류 advice에는 확장 과제를 위한 오류 운송과 malformed JSON 처리만 추가했다.

## 추가 및 변경 파일

| 위치 | 내용 |
| --- | --- |
| settings.gradle, gradle/libs.versions.toml | 독립 서비스 2개와 테스트/Kafka/JDBC 의존성 |
| lab-steps.csv, lab.properties | 전역 Step 등록부, 활성 상한 |
| banking-domain/src/main/java/com/corebanking/contracts | 이체·원장·Saga·이벤트·Outbox·파생 데이터·대사 및 workload 계약 |
| banking-application/src/main/java/com/corebanking/lab | 금융 서비스·생성기·매핑·측정 TODO 스켈레톤 |
| banking-api/src/main/java/com/corebanking/lab | 확장 HTTP DTO/라우팅 |
| banking-api/src/main/resources/application-lab-core.yml | 확장 API와 추가 migration 프로필 |
| banking-infrastructure/src/main/resources/db/lab-core | 원자성·대사 검증용 데이터 스키마 |
| banking-infrastructure/src/main/java/com/corebanking/lab | Kafka 발행 포트 TODO |
| transfer-orchestrator | 독립 Boot 서비스, HTTP 포트 TODO, workflow DB 스키마 |
| banking-projections | 독립 Boot 서비스, Kafka listeners/소비 TODO, HTTP 증거 포트, 파생 DB 스키마 |
| banking-lab-tests/src/test/java/com/corebanking/lab/assignment02~12 | 새 기능 Step 81개 |
| banking-lab-tests/src/test/java/com/corebanking/lab/support | 자금 공급, 실제 프로세스, Kafka, 장애 주입, 합성 데이터 Fixture |
| banking-lab-tests/src/test/java/com/corebanking/lab/environment | 선택적 인프라 smoke test 추가 |
| banking-lab-tests/build.gradle, scripts/lab | 단계 선택·누적 회귀·0개 실패·실제 결과 status·doctor |
| docs/assignments/02~12 | 각 과제의 목표/API/TODO/테스트/불변식/실행/완료/연결 |
| docs/api-contracts.md, docs/workload-guide.md, docs/lab-catalog.md | 공통 계약과 측정/데이터 출처 규칙 |
| README.md, AGENTS.md, docs/architecture.md, docs/testing-guide.md | 확장 구조·활성화·규칙 및 실행법 |

각 과제의 API와 전체 테스트 목록은 [Lab Catalog](lab-catalog.md) 및 [Step 등록부](../lab-steps.csv), 모듈 의존성은 [Architecture](architecture.md)에 기재했다.

## 실제 실행 결과

| 검증 | 결과 |
| --- | --- |
| Java / Gradle / Docker | OpenJDK 21.0.12.1 / cached Gradle 8.7 / Docker 25.0.3 |
| compileTestJava | PASS: 전체 모듈 및 제공 테스트 컴파일 |
| Core / Orchestrator / Projections bootJar | PASS: 독립 실행 JAR 생성 |
| environmentTest | 2/2 PASS: PostgreSQL/Flyway/Spring 및 기존 UUID 오류 계약 |
| infrastructureTest (labInfrastructure=10) | PASS: 3개 독립 프로세스, 별도 DB migration, Core 접근 차단, Kafka 송수신, 503/타임아웃/reset 주입, 실제 프로세스 재시작 |
| infrastructureTest (labInfrastructure=08) | PASS: Kafka 및 분리 소비자 Fixture topic 환경 |
| initialFailureTest (최종 소스) | 12/12 의도한 기능 실패, 환경 오류 0 |
| 전체 regressionTest | 86개 실행; 기능 미구현에 따른 85개 실패; 06-08 아키텍처 테스트 PASS; 환경 오류 0 |
| 기본 gradlew test | PASS: lab 기능 테스트 SKIPPED, 미래 기능 테스트 미실행 |
| scripts/lab doctor | PASS: 실제 Java/Gradle/Docker 진단과 PostgreSQL 환경 테스트 |
| scripts/lab test 03-02 | 한 Step만 선택, TODO: A03에 의한 예상 실패 |
| scripts/lab status | 실제 실행 결과 및 소스 변경에 따른 STALE 표시 |
| 상한 02 / 05 | Gradle dry-run 성공; 같은 범위 선택기의 status 상한 05에서 01~05의 33개만 표시 |
| scripts/lab test 02-99 | exit 2: Unknown step |
| Gradle assignmentTest -PlabStep=99-99 | exit 1: No registered test targets selected |
| 스크립트 bash -n | PASS |
| 실행 종료 후 자식 금융 서비스 프로세스 | 잔존 프로세스 없음 |

전체 회귀 검증 후 공통 Fixture와 독립 실행 설정을 보강하여 환경·인프라·각 과제 첫 Step을 최종 소스로 재검증했다. 따라서 먼저 실행된 다른 Step의 status는 STALE로 표시될 수 있으며 과거 결과를 현재 소스의 PASS로 취급하지 않는다.
JUnit XML을 파싱해 전체 회귀 86개, 실패 85개, 첫 Step 12개의 명시적 TODO 메시지, 환경 오류 0개를 확인했다. 기존 A01 일부 assertion과 성공 요청 개수 assertion은 응답 본문을 failure message에 포함하지 않으므로 status가 FUNCTIONAL_FAIL로 기록될 수 있다. 이것도 환경 오류와 구별되는 초기 기능 실패이다.
잘못된 선택은 등록부 검증과 실행 전 빈 대상 검사에서 실패하며, suite 종료 시 testCount=0도 실패하도록 구성했다.

## 의도한 첫 기능 실패

| Step | 최초 응답/실패 |
| --- | --- |
| 01-01 | HTTP 501, TODO: A01 |
| 02-01 | HTTP 501, TODO: A02 |
| 03-01 | HTTP 501, TODO: A03 |
| 04-01 | HTTP 501, TODO: A04 |
| 05-01 | HTTP 501, TODO: A05 |
| 06-01 | HTTP 501, TODO: A06 |
| 07-01 | HTTP 501, TODO: A07 |
| 08-01 | HTTP 501, TODO: A08 |
| 09-01 | HTTP 501, TODO: A09 |
| 10-01 | HTTP 501, TODO: A10 |
| 11-01 | HTTP 501, TODO: A11 |
| 12-01 | HTTP 501, TODO: A12 |

A03~11의 일부 첫 테스트는 선행 이체 기능에서 먼저 실패한다. 선행 기능을 구현하면 원장·Kafka·대사 등의 후속 assertion까지 진행한다.
A12-06의 생성기 계약은 UnsupportedOperationException("TODO: A12 generator")로 직접 실패한다.

## 학습자가 구현할 TODO

| 영역 | 구현 위치 및 책임 |
| --- | --- |
| Account | 기존 AccountServiceImpl / AccountRepository |
| Transfer | TransferServiceSkeleton; 검증, 단일 Core 트랜잭션, 영속화 |
| Ledger | LedgerServiceSkeleton; 원장 쌍, 불변 조회, 초기 baseline 대사 |
| Concurrency | Core 금융 서비스의 동시성 및 충돌 복구 전략 |
| Idempotency | Core 금융 효과와 키/지문/응답의 원자적 저장 |
| Service boundary | OrchestrationSkeleton / HttpCoreBankingAdapter |
| Saga | WorkflowSkeleton / validation·followup HTTP adapters; 상태 영속화, 불확실 결과 확인 |
| Kafka | KafkaEventPublisherSkeleton / ProjectionSkeleton.consume; 독립 소비자, 중복 효과 방지 |
| Outbox | OutboxServiceSkeleton; 커밋 기록, 최종 발행, 중복 전달·재시작 처리 |
| Recovery | WorkflowService.recover 및 소비/발행 서비스의 미완료 작업 복구, MANUAL_REVIEW |
| Reconciliation | LedgerService.reconcile / ProjectionService.reconcile / HttpCoreEvidenceAdapter |
| Workload | DatasetAdapter 구현, TransactionTypeMapperSkeleton, WorkloadGeneratorSkeleton, 초기 잔액/계좌 매핑 정책, WorkloadServiceSkeleton, WorkloadMetricsSkeleton |

모든 업무 메서드는 TODO이며 정답 트랜잭션, 잠금, 멱등성, Saga, Outbox, 대사, 복구 및 최적화 알고리즘을 구현하지 않았다.

## 검증 한계

금융 업무 메서드가 TODO이므로 정상 이체 이후 원장/비동기/복구 assertion의 실제 통과 여부는 아직 검증할 수 없다. Fixture의 실행과 초기 실패를 검증했으며 정답 구현의 성능이나 금융 정합성을 보증하는 결과는 아니다.
WireMock 지연/reset 및 프로세스 재시작은 독립 인프라 테스트로 검증했다. 실제 금융 커밋 이후 응답 유실/업무 도중 종료 시나리오의 최종 정합성은 선행 과제 구현 후 기능 테스트로 검증해야 한다.
외부 금융 데이터셋은 포함하거나 실행하지 않았다. 실제 처리량/백분위 지연/DB Lock Wait/Deadlock/복구 시간 측정값과 성능 합격 임계값은 아직 없다.
기존 Unix Gradle bootstrap을 유지했다. 표준 wrapper JAR가 아니며 fallback 8.10.2의 신규 다운로드 경로와 Windows 실행은 이번 검증 범위에 포함하지 않았다.

## 주요 실행 명령

```bash
./scripts/lab doctor
./scripts/lab test 02-01
./scripts/lab test 03-02
./scripts/lab verify 02
./scripts/lab verify 05
./scripts/lab verify all
./scripts/lab status
./gradlew :banking-lab-tests:infrastructureTest -PlabInfrastructure=10
./gradlew :banking-lab-tests:initialFailureTest
```

전체 회귀 HTML: banking-lab-tests/build/reports/tests/regressionTest/index.html.
실제 Step 결과: banking-lab-tests/build/lab-results/*.properties.
