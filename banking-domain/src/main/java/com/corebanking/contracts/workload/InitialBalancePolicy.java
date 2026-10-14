package com.corebanking.contracts.workload;

import java.util.Map;
import java.util.UUID;

public interface InitialBalancePolicy {
    Map<UUID, Long> openingBalances(Map<String, UUID> accountMapping, String provenance);
}
