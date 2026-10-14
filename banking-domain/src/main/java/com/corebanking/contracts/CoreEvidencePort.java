package com.corebanking.contracts;

public interface CoreEvidencePort {
    FinancialSnapshot snapshot();
    void requestEventReplay(java.util.UUID transferId);
}
