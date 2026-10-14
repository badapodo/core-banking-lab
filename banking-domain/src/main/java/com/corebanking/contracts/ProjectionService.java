package com.corebanking.contracts;

import java.util.UUID;

public interface ProjectionService {
    void consume(String consumer, TransferCommitted event);
    ProjectionResult find(String consumer, UUID eventId);
    ReconciliationReport reconcile(boolean repair);
    record ProjectionResult(UUID eventId, UUID transferId, int effectCount, long sequence, int deliveryCount) {}
}
