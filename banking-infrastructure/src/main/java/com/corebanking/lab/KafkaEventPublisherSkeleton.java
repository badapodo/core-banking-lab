package com.corebanking.lab;

import com.corebanking.contracts.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("lab-core")
public class KafkaEventPublisherSkeleton implements FinancialEventPublisher {
    public void publish(TransferCommitted event) { throw new UnsupportedOperationException("TODO: A08 event publication"); }
}
