package com.corebanking.contracts.workload;

import java.util.UUID;

public interface AccountIdentifierMapper {
    UUID map(String sourceIdentifier, long seed);
}
