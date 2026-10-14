package com.corebanking.lab;

import com.corebanking.contracts.workload.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("lab-core")
public class WorkloadGeneratorSkeleton implements WorkloadGenerator {
    public Workload generate(DatasetAdapter adapter, long seed) { throw new UnsupportedOperationException("TODO: A12 generator"); }
}
