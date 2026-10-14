package com.corebanking.contracts;

import java.util.List;

public record FinancialSnapshot(List<TransferResult> transfers,
                               List<LedgerService.LedgerEntry> ledger,
                               List<OutboxService.OutboxRecord> outbox) {}
