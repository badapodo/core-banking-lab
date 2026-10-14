package com.corebanking.lab.support;

import com.corebanking.contracts.workload.DatasetAdapter;
import java.util.stream.Stream;

public final class SyntheticDatasetFixture implements DatasetAdapter {
    public Stream<DatasetRow> read() {
        return Stream.of(new DatasetRow("A","B","TRANSFER",20,true,"SYNTHETIC"),
                         new DatasetRow("B","C","TRANSFER",10,true,"SYNTHETIC"),
                         new DatasetRow("C","A","UNSUPPORTED",5,true,"SYNTHETIC"));
    }
}
