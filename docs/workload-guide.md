# Financial Workload

## Dataset contracts

DatasetAdapter reads sourceId, targetId, type, amount, synthetic and provenance. External dataset adapters are learner supplied; no actual dataset is bundled.
TransactionTypeMapper maps known internal transfers to INTERNAL_TRANSFER and nonrepresentable types to EXCLUDED. Unsupported types must not be silently relabeled as observed bank transfers.
WorkloadGenerator uses a supplied seed, deterministic account mapping and explicit opening balances. A stream is consumed once; provide a fresh adapter for another run.
SyntheticDatasetFixture contains A→B 20, B→C 10 and one unsupported row. Generator contract expects two eligible transfers, three mapped accounts, nonnegative opening balances and SYNTHETIC provenance.
Missing timestamps, balances, beneficiary identity or transaction outcomes must stay missing, or be explicitly labeled synthetic/estimated. Do not report them as observed values.

## Workload API

POST /api/v1/workloads accepts seed, accounts, requests, concurrency, initialBalance and dataset.
The synthetic run supplies the requested number of accounts with the declared opening balance, recorded through the baseline convention.
accountMapping represents deterministic logical identities; repeats with the same seed reuse the same logical mapping. A run's physical data/reset policy must avoid changing already confirmed financial history.
All internal transfers conserve the total supplied opening liquidity. Use the existing financial boundary for every request; the workload runner has no extra authority to bypass financial checks.

## Metrics

WorkloadResult includes completed, failed, throughput, p50Millis, p95Millis, p99Millis, failureRate, lockWaitMillis, deadlocks, idempotencyViolations, conservationViolations, ledgerMismatches, recoveryMillis, seed, accountMapping, provenance.
Request latency uses a monotonic clock and includes the entire client-observed request. Percentiles are computed from recorded request samples using a documented percentile convention. Failure rate uses all submitted requests as denominator.
DB Lock Wait and deadlocks must come from PostgreSQL observations and a declared measurement window. Recovery time spans fault injection to restored observable progress. Zero is valid only when actually measured or when the run includes no recovery scenario; annotate measurement coverage in the accompanying report.
Do not use HTTP response percentiles as substitutes for DB lock wait.

## Measurement report

Record date, commit/source fingerprint, Java/Gradle/PostgreSQL/Kafka versions, machine CPU/RAM, container limits, network topology, dataset provenance, seed, opening liquidity, request count, concurrency, warmup, run duration, collector interval and raw sample location.
Record units, percentile method, excluded requests and recovery fault scenario. Keep correctness checks and measurements distinct.
No arbitrary throughput or latency threshold is supplied. Approve a target only alongside a fixed measurement environment.
The lab provides DTOs, interfaces, synthetic rows and HTTP contract tests; workload generation/execution/collectors are TODOs. It is not a completed benchmark.

## Run

```bash
./scripts/lab test 12-01
./scripts/lab test 12-03
./scripts/lab verify 12
```
