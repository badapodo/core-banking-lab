package com.banklab.orchestrator;

import com.corebanking.contracts.WorkflowFollowupPort;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class HttpFollowupAdapter implements WorkflowFollowupPort {
    public void complete(UUID transferId) { throw new UnsupportedOperationException("TODO: A07 followup"); }
}
