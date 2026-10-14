# API Contracts

## Scope and activation

A01 retains its original API and zero opening balance. A02+ endpoints are active only under Spring profile `lab-core`. Future infrastructure is not started by the default application or default Gradle test task. Tests set lab.assignment only to label the current TODO response; it does not select an algorithm.

Amounts are positive integer KRW (long), identifiers UUID, timestamps UTC ISO-8601. Client validation is part of the learner TODO, so invalid financial requests initially return 501 rather than a preimplemented solution.

## Financial API

| Endpoint | Success | Body |
| --- | --- | --- |
| POST /api/v1/transfers | 201 | TransferResult |
| GET /api/v1/transfers/{id} | 200 | TransferResult |
| GET /api/v1/transfers/by-key/{key} | 200 | TransferResult |
| GET /api/v1/accounts/{id}/ledger | 200 | LedgerEntry array |
| POST /api/v1/reconciliation/core | 200 | ReconciliationReport |
| GET /api/v1/reconciliation/snapshot | 200 | transfers, ledger, outbox arrays |
| GET /api/v1/outbox?transferId={id} | 200 | OutboxRecord array |
| POST /api/v1/outbox/publish | 200 | Empty body |
| POST /api/v1/outbox/replay | 200 | Empty body; request {transferId} |
| POST /api/v1/workloads | 200 | WorkloadResult |

TransferCommand: sourceAccountId, targetAccountId, amount.
TransferResult: transferId, sourceAccountId, targetAccountId, amount, status=COMMITTED, committedAt.
LedgerEntry: entryId, transferId, accountId, side=DEBIT|CREDIT, amount.
OutboxRecord: eventId, transferId, state=PENDING|PUBLISHING|PUBLISHED, attempts.

A02~04 may omit Idempotency-Key. From A05, supplied keys must be persisted atomically with the financial effect. Same key and identical command returns the same 201 response body including ID and time. A different command conflicts with 409. Required keys on Saga requests preserve workflow identity too.

## Errors

Every business error has `{"code":"...","message":"..."}`. Messages are descriptive; tests compare code and status.

| Status | Code | Meaning |
| --- | --- | --- |
| 400 | INVALID_REQUEST | Malformed UUID/request |
| 400 | INVALID_AMOUNT | Zero or negative amount |
| 400 | SAME_ACCOUNT | Same source and target |
| 400 | DATASET_UNAVAILABLE | Dataset not supplied |
| 404 | ACCOUNT_NOT_FOUND | Missing financial account |
| 404 | TRANSFER_NOT_FOUND | Unknown transfer/key |
| 404 | EVENT_NOT_FOUND | Event not yet observed |
| 409 | INSUFFICIENT_FUNDS | Debit cannot be funded |
| 409 | IDEMPOTENCY_CONFLICT | Same key, different command |
| 409 | RETRYABLE_CONFLICT | Rolled-back concurrency conflict |
| 503 | FINANCIAL_WRITE_FAILED | DB failure with full rollback |
| 503 | UPSTREAM_UNAVAILABLE | Service/network unavailable |
| 504 | UPSTREAM_TIMEOUT | Deadline exceeded |
| 501 | TODO | Explicit unimplemented lab operation |

`LabException` provides only error transport. No financial validation, locking, deduplication or recovery is implemented.

## Orchestrator API

POST/GET /api/v1/transfers preserves the financial API through CoreBankingPort HTTP calls.
POST /api/v1/sagas returns 202 and SagaResult; GET /api/v1/sagas/{id} returns 200.
POST /api/v1/recovery/run returns 200 once a recovery pass is requested.

SagaResult: sagaId, transferId (null until resolved), state, attempts, lastError, stateHistory.
States: RECEIVED, VALIDATING, CORE_PENDING, COMMITTED, COMPLETED, REJECTED, RETRY_PENDING, FOLLOWUP_PENDING, MANUAL_REVIEW.
The accepted response identifies durable work. Normal history contains RECEIVED, VALIDATING, CORE_PENDING, COMMITTED, COMPLETED in order. COMMITTED is a financial fact that followup failure never reverses.
External validation: GET {lab.validation-url}/recipients/{accountId} returns {"valid":true|false}.
External followup: POST {lab.validation-url}/followups with transferId returns 200.
Timeout config lab.http-timeout-millis=1000; retry limit lab.retry-limit=3.

## Events and projections

TransferCommitted schemaVersion=1:
eventId, transferId, sourceAccountId, targetAccountId, amount, sequence, occurredAt.
The original event ID survives retries/replays. Kafka key is transferId; consumers must tolerate duplicates and out-of-order business sequences.
GET /api/v1/consumers/{audit|notifications}/events/{eventId} returns eventId, transferId, effectCount, sequence, deliveryCount.
deliveryCount counts observed deliveries; effectCount counts applied effects. Their distinction lets tests wait for the duplicate before asserting no extra effect.
POST /api/v1/reconciliation/distributed?repair=false|true returns ReconciliationReport.
ReconciliationReport: status=CONSISTENT|INCONSISTENT, discrepancies (strings with category and IDs), repaired.
Categories include MISSING_OUTBOX, MISSING_CONSUMPTION, DUPLICATE_EFFECT. Derivative repair uses CoreEvidencePort and event replay, never direct Core DB writes.

## Observable storage

Flyway supplies storage contracts for integration inspection:
Core: accounts, funding_baselines, transfers, ledger_entries, idempotency_records, outbox_events.
Orchestrator: sagas.
Projections: consumed_events.

These names describe observable state. Repository APIs, ORM choice, SQL writes, locking strategy, scheduling and deduplication algorithms remain learner choices. Additional internal tables/columns are allowed.
