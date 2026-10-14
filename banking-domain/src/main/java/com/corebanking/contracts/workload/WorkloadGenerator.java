package com.corebanking.contracts.workload;

import com.corebanking.contracts.TransferCommand;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WorkloadGenerator {
    Workload generate(DatasetAdapter adapter, long seed);
    record Workload(Map<String, UUID> accountMapping, Map<UUID, Long> openingBalances,
                    List<TransferCommand> transfers, long seed, String provenance) {}
}
