package com.corebanking.contracts;

public interface FinancialEventPublisher {
    void publish(TransferCommitted event);
}
