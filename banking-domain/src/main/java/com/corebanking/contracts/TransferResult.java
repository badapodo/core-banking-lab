package com.corebanking.contracts;

import java.util.UUID;
import java.time.Instant;

public record TransferResult(UUID transferId, UUID sourceAccountId, UUID targetAccountId,
                             long amount, String status, Instant committedAt) {}
