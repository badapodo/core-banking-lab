package com.corebanking.contracts;

import java.time.Instant;
import java.util.UUID;

public record TransferCommitted(UUID eventId, int schemaVersion, UUID transferId,
                                UUID sourceAccountId, UUID targetAccountId, long amount,
                                long sequence, Instant occurredAt) {}
