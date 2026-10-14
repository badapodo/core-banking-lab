package com.corebanking.account.dto;

import com.corebanking.account.AccountResult;
import com.corebanking.account.AccountStatus;

import java.util.UUID;

public record AccountResponse(
        UUID accountId,
        long balance,
        AccountStatus status
) {
    public static AccountResponse from(AccountResult result) {
        return new AccountResponse(result.accountId(), result.balance(), result.status());
    }
}
