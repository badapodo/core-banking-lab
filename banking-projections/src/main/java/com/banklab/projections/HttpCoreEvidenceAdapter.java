package com.banklab.projections;

import com.corebanking.contracts.*;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class HttpCoreEvidenceAdapter implements CoreEvidencePort {
    public FinancialSnapshot snapshot() { throw new UnsupportedOperationException("TODO: A11 Core HTTP snapshot"); }
    public void requestEventReplay(UUID transferId) { throw new UnsupportedOperationException("TODO: A11 event replay"); }
}
