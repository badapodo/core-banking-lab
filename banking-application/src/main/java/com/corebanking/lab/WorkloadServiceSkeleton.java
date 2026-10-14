package com.corebanking.lab;

import com.corebanking.contracts.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("lab-core")
public class WorkloadServiceSkeleton implements com.corebanking.contracts.workload.WorkloadService {
    @Value("${lab.assignment:02}") private String assignment;
    private UnsupportedOperationException todo() {
        return new UnsupportedOperationException("TODO: A" + assignment);
    }
    public WorkloadResult run(WorkloadRequest request) { throw todo(); }
}
