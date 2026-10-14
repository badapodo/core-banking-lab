package com.corebanking.lab;

import com.corebanking.contracts.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("lab-core")
public class LedgerServiceSkeleton implements LedgerService {
    @Value("${lab.assignment:02}") private String assignment;
    private UnsupportedOperationException todo() {
        return new UnsupportedOperationException("TODO: A" + assignment);
    }
    public java.util.List<LedgerEntry> entries(java.util.UUID id) { throw todo(); }
    public ReconciliationReport reconcile() { throw todo(); }
    public FinancialSnapshot snapshot() { throw todo(); }
}
