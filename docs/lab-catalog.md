# Lab Catalog

| Assignment | API / 계약 | Steps | 문서 |
| --- | --- | --- | --- |
| 01 Account | POST/GET accounts | 01-01~01-05 | [Account](assignments/01-account.md) |
| 02 Transfer | `TransferService`, `TransferCommand`, `TransferResult`; POST `/api/v1/transfers`, GET `/api/v1/transfers/{id}`. | 02-01~02-07 | [Transfer](assignments/02-transfer.md) |
| 03 Ledger | `LedgerService`, `LedgerEntry`; GET `/api/v1/accounts/{id}/ledger`, POST `/api/v1/reconciliation/core`. | 03-01~03-07 | [Ledger](assignments/03-ledger.md) |
| 04 Concurrency | Assignment 02 API 유지; `RETRYABLE_CONFLICT` 409 계약 추가. | 04-01~04-08 | [Concurrency](assignments/04-concurrency.md) |
| 05 Idempotency | POST `/api/v1/transfers`의 `Idempotency-Key`; GET `/api/v1/transfers/by-key/{key}`. | 05-01~05-06 | [Idempotency](assignments/05-idempotency.md) |
| 06 Service Boundary | Orchestrator의 POST/GET `/api/v1/transfers`; `CoreBankingPort`, `HttpCoreBankingAdapter`. | 06-01~06-08 | [Service Boundary](assignments/06-service-boundary.md) |
| 07 Saga | POST `/api/v1/sagas` 202, GET `/api/v1/sagas/{id}` 200, POST `/api/v1/recovery/run` 200; `WorkflowService`, validation/followup 포트. | 07-01~07-09 | [Saga](assignments/07-saga.md) |
| 08 Kafka Messaging | `TransferCommitted`, `ProjectionService`; GET `/api/v1/consumers/{consumer}/events/{eventId}`; audit/notifications 그룹. | 08-01~08-08 | [Kafka Messaging](assignments/08-kafka.md) |
| 09 Transactional Outbox | `OutboxService`; GET `/api/v1/outbox?transferId=...`, POST `/api/v1/outbox/publish`, POST `/api/v1/outbox/replay` body `{transferId}`. | 09-01~09-07 | [Transactional Outbox](assignments/09-outbox.md) |
| 10 Recovery | Saga 조회와 POST `/api/v1/recovery/run`; lastError, attempts, MANUAL_REVIEW 계약. | 10-01~10-08 | [Recovery](assignments/10-recovery.md) |
| 11 Reconciliation | POST `/api/v1/reconciliation/core`; GET `/api/v1/reconciliation/snapshot`; POST `/api/v1/reconciliation/distributed?repair=false|true`; `CoreEvidencePort`. | 11-01~11-07 | [Reconciliation](assignments/11-reconciliation.md) |
| 12 Financial Workload | `DatasetAdapter`, `TransactionTypeMapper`, `WorkloadGenerator`, `WorkloadService`; POST `/api/v1/workloads`. | 12-01~12-06 | [Financial Workload](assignments/12-financial-workload.md) |

총 86개 Step. 전체 클래스 및 시나리오는 [lab-steps.csv](../lab-steps.csv)에 등록되어 있습니다. 각 Assignment 문서에는 학습 목표, 사전 요구사항, API, TODO, 테스트 목록, 불변식, 실행법, 완료 조건 및 다음 과제 연결을 기재했습니다.

기존 A01 문서는 보존했습니다. A01 구현 TODO는 AccountServiceImpl와 AccountRepository이며 완료 조건은 기존 5개 Step과 환경 테스트 통과입니다. A02에서는 별도 테스트 Fixture가 초기 잔액 0원 계좌 계약을 변경하지 않고 이체 자금을 공급합니다.
