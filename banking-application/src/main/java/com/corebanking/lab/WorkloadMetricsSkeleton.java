package com.corebanking.lab;

import com.corebanking.contracts.workload.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("lab-core")
public class WorkloadMetricsSkeleton implements WorkloadMetricsCollector {
    public void start(MeasurementEnvironment environment) { throw new UnsupportedOperationException("TODO: A12 metrics"); }
    public void requestCompleted(long elapsedNanos, boolean success) { throw new UnsupportedOperationException("TODO: A12 metrics"); }
    public WorkloadService.WorkloadResult finish() { throw new UnsupportedOperationException("TODO: A12 metrics"); }
}
