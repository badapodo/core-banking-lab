package com.corebanking.lab;

import com.corebanking.contracts.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("lab-core")
public class TransferServiceSkeleton implements TransferService {
    @Value("${lab.assignment:02}") private String assignment;
    private UnsupportedOperationException todo() {
        return new UnsupportedOperationException("TODO: A" + assignment);
    }
    public TransferResult transfer(TransferCommand command, String key) { throw todo(); }
    public TransferResult find(java.util.UUID id) { throw todo(); }
    public TransferResult findByKey(String key) { throw todo(); }
}
