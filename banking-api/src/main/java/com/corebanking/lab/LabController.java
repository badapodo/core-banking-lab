package com.corebanking.lab;

import com.corebanking.contracts.*;
import com.corebanking.contracts.workload.WorkloadService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@RestController
@Profile("lab-core")
public class LabController {
    private final TransferService transfers;
    private final LedgerService ledger;
    private final OutboxService outbox;
    private final WorkloadService workload;
    public LabController(TransferService transfers, LedgerService ledger, OutboxService outbox,
                         WorkloadService workload) {
        this.transfers = transfers; this.ledger = ledger; this.outbox = outbox; this.workload = workload;
    }
    @GetMapping("/lab/health") public String health() { return "UP"; }
    @PostMapping("/api/v1/transfers")
    public ResponseEntity<TransferResult> transfer(@RequestBody TransferCommand request,
            @RequestHeader(value = "Idempotency-Key", required = false) String key) {
        return ResponseEntity.status(201).body(transfers.transfer(request, key));
    }
    @GetMapping("/api/v1/transfers/{id}") public TransferResult find(@PathVariable UUID id) {
        return transfers.find(id);
    }
    @GetMapping("/api/v1/transfers/by-key/{key}") public TransferResult byKey(@PathVariable String key) {
        return transfers.findByKey(key);
    }
    @GetMapping("/api/v1/accounts/{id}/ledger") public List<LedgerService.LedgerEntry> ledger(@PathVariable UUID id) {
        return ledger.entries(id);
    }
    @PostMapping("/api/v1/reconciliation/core") public ReconciliationReport reconcile() { return ledger.reconcile(); }
    @GetMapping("/api/v1/reconciliation/snapshot") public FinancialSnapshot snapshot() { return ledger.snapshot(); }
    @GetMapping("/api/v1/outbox") public List<OutboxService.OutboxRecord> outbox(@RequestParam UUID transferId) {
        return outbox.records(transferId);
    }
    @PostMapping("/api/v1/outbox/publish") public void publish() { outbox.publishPending(); }
    @PostMapping("/api/v1/outbox/replay") public void replay(@RequestBody ReplayRequest request) { outbox.replay(request.transferId()); }
    public record ReplayRequest(UUID transferId) {}
    @PostMapping("/api/v1/workloads") public WorkloadService.WorkloadResult workload(@RequestBody WorkloadService.WorkloadRequest request) {
        return workload.run(request);
    }
}
