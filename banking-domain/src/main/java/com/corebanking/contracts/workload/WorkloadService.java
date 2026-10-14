package com.corebanking.contracts.workload;

import java.util.List;

public interface WorkloadService {
    WorkloadResult run(WorkloadRequest request);
    record WorkloadRequest(long seed, int accounts, int requests, int concurrency,
                           long initialBalance, String dataset) {}
    record WorkloadResult(long seed, List<String> accountMapping, int completed, int failed,
                          double throughput, double p50Millis, double p95Millis, double p99Millis,
                          double failureRate, long lockWaitMillis, int deadlocks,
                          int idempotencyViolations, int conservationViolations,
                          int ledgerMismatches, long recoveryMillis, String provenance) {}
}
