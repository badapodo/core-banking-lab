package com.corebanking.account;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    @Override
    public AccountResult createAccount() {
        throw new UnsupportedOperationException("TODO: A01");
    }

    @Override
    public AccountResult getAccount(UUID accountId) {
        throw new UnsupportedOperationException("TODO: A01");
    }
}
