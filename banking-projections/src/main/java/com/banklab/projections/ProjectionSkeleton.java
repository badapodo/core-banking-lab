package com.banklab.projections;

import com.corebanking.contracts.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ProjectionSkeleton implements ProjectionService {
    @Value("${lab.assignment:08}") private String assignment;
    private UnsupportedOperationException todo() { return new UnsupportedOperationException("TODO: A" + assignment); }
    public void consume(String consumer, TransferCommitted event) { throw todo(); }
    public ProjectionResult find(String consumer, UUID eventId) { throw todo(); }
    public ReconciliationReport reconcile(boolean repair) { throw todo(); }
}
