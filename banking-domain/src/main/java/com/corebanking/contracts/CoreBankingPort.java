package com.corebanking.contracts;

public interface CoreBankingPort {
    TransferResult transfer(TransferCommand command, String key);
    TransferResult findByKey(String key);
}
