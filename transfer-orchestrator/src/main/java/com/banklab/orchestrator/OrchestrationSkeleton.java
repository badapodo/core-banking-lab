package com.banklab.orchestrator;

import com.corebanking.contracts.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class OrchestrationSkeleton implements TransferService {
    @Value("${lab.assignment:06}") private String assignment;
    private UnsupportedOperationException todo() { return new UnsupportedOperationException("TODO: A" + assignment); }
    public TransferResult transfer(TransferCommand command, String key) { throw todo(); }
    public TransferResult find(UUID id) { throw todo(); }
    public TransferResult findByKey(String key) { throw todo(); }
}
