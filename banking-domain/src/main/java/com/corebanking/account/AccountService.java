package com.corebanking.account;

import java.util.UUID;

public interface AccountService {

    AccountResult createAccount();

    AccountResult getAccount(UUID accountId);
}
