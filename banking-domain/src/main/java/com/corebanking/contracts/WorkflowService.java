package com.corebanking.contracts;

import java.util.UUID;

public interface WorkflowService {
    SagaResult start(TransferCommand command, String idempotencyKey);
    SagaResult find(UUID sagaId);
    void recover();
    record SagaResult(UUID sagaId, UUID transferId, String state, int attempts, String lastError,
                      java.util.List<String> stateHistory) {}
}
