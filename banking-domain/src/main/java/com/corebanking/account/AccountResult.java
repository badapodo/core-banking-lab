package com.corebanking.account;

import java.util.UUID;

public record AccountResult(
        UUID accountId,
        long balance,
        AccountStatus status
) {
}
