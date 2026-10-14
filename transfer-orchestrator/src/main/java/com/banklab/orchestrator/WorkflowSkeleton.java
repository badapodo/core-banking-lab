package com.banklab.orchestrator;

import com.corebanking.contracts.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class WorkflowSkeleton implements WorkflowService {
    @Value("${lab.assignment:07}") private String assignment;
    private UnsupportedOperationException todo() { return new UnsupportedOperationException("TODO: A" + assignment); }
    public SagaResult start(TransferCommand command, String key) { throw todo(); }
    public SagaResult find(UUID id) { throw todo(); }
    public void recover() { throw todo(); }
}
