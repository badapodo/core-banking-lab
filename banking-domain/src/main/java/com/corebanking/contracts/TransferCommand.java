package com.corebanking.contracts;

import java.util.UUID;

public record TransferCommand(UUID sourceAccountId, UUID targetAccountId, long amount) {}
