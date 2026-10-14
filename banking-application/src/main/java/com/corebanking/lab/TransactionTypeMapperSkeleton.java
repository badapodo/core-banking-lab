package com.corebanking.lab;

import com.corebanking.contracts.workload.TransactionTypeMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("lab-core")
public class TransactionTypeMapperSkeleton implements TransactionTypeMapper {
    public Type map(String datasetType) { throw new UnsupportedOperationException("TODO: A12 type mapping"); }
}
