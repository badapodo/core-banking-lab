package com.corebanking.contracts;

import java.util.UUID;

public interface TransferService {
    TransferResult transfer(TransferCommand command, String idempotencyKey);
    TransferResult find(UUID transferId);
    TransferResult findByKey(String idempotencyKey);
}
