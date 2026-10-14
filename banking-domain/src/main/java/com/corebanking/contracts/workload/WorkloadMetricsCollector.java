package com.corebanking.contracts.workload;

import java.time.Instant;

public interface WorkloadMetricsCollector {
    void start(MeasurementEnvironment environment);
    void requestCompleted(long elapsedNanos, boolean success);
    WorkloadService.WorkloadResult finish();
    record MeasurementEnvironment(Instant startedAt, String javaVersion, String postgresVersion,
                                  int cpuCount, long memoryBytes, long seed, int concurrency,
                                  String topology, String provenance, String percentileMethod) {}
}
