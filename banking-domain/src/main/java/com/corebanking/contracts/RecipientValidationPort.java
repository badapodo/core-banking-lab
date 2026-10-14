package com.corebanking.contracts;

import java.util.UUID;

public interface RecipientValidationPort {
    boolean validate(UUID recipientId);
}
