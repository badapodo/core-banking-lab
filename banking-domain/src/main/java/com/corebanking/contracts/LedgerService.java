package com.corebanking.contracts;

import java.util.List;
import java.util.UUID;

public interface LedgerService {
    List<LedgerEntry> entries(UUID accountId);
    ReconciliationReport reconcile();
    FinancialSnapshot snapshot();
    record LedgerEntry(UUID entryId, UUID transferId, UUID accountId, String side, long amount) {}
}
