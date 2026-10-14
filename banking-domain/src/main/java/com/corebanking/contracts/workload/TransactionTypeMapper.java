package com.corebanking.contracts.workload;

public interface TransactionTypeMapper {
    enum Type { INTERNAL_TRANSFER, EXCLUDED }
    Type map(String datasetType);
}
