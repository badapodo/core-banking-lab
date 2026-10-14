package com.corebanking.contracts.workload;

import java.util.stream.Stream;

public interface DatasetAdapter {
    Stream<DatasetRow> read();
    record DatasetRow(String sourceId, String targetId, String type, long amount,
                      boolean synthetic, String provenance) {}
}
