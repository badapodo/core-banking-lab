package com.corebanking.account;

import java.util.UUID;

public class Account {

    private final UUID accountId;
    private final long balance;
    private final AccountStatus status;

    public Account(UUID accountId, long balance, AccountStatus status) {
        this.accountId = accountId;
        this.balance = balance;
        this.status = status;
    }

    public UUID accountId() {
        return accountId;
    }

    public long balance() {
        return balance;
    }

    public AccountStatus status() {
        return status;
    }
}
