package com.banklab.orchestrator;

import com.corebanking.contracts.RecipientValidationPort;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class HttpRecipientValidationAdapter implements RecipientValidationPort {
    public boolean validate(UUID recipientId) { throw new UnsupportedOperationException("TODO: A07 validation"); }
}
