package com.banklab.orchestrator;

import com.corebanking.contracts.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
public class OrchestratorController {
    private final TransferService transfers;
    private final WorkflowService workflow;
    public OrchestratorController(TransferService transfers, WorkflowService workflow) {
        this.transfers = transfers; this.workflow = workflow;
    }
    @GetMapping("/lab/health") public String health() { return "UP"; }
    @PostMapping("/api/v1/transfers") public ResponseEntity<TransferResult> transfer(@RequestBody TransferCommand request,
            @RequestHeader(value = "Idempotency-Key", required = false) String key) {
        return ResponseEntity.status(201).body(transfers.transfer(request, key));
    }
    @GetMapping("/api/v1/transfers/{id}") public TransferResult find(@PathVariable UUID id) { return transfers.find(id); }
    @PostMapping("/api/v1/sagas") public ResponseEntity<WorkflowService.SagaResult> saga(@RequestBody TransferCommand request,
            @RequestHeader("Idempotency-Key") String key) {
        return ResponseEntity.status(202).body(workflow.start(request, key));
    }
    @GetMapping("/api/v1/sagas/{id}") public WorkflowService.SagaResult saga(@PathVariable UUID id) { return workflow.find(id); }
    @PostMapping("/api/v1/recovery/run") public void recover() { workflow.recover(); }
}
