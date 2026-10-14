package com.corebanking.lab;

import com.corebanking.contracts.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("lab-core")
public class OutboxServiceSkeleton implements OutboxService {
    @Value("${lab.assignment:02}") private String assignment;
    private UnsupportedOperationException todo() {
        return new UnsupportedOperationException("TODO: A" + assignment);
    }
    public java.util.List<OutboxRecord> records(java.util.UUID id) { throw todo(); }
    public void publishPending() { throw todo(); }
    public void replay(java.util.UUID transferId) { throw todo(); }
}
