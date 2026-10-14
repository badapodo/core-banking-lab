package com.corebanking.contracts;

import java.util.List;
import java.util.UUID;

public interface OutboxService {
    List<OutboxRecord> records(UUID transferId);
    void publishPending();
    void replay(UUID transferId);
    record OutboxRecord(UUID eventId, UUID transferId, String state, int attempts) {}
}
