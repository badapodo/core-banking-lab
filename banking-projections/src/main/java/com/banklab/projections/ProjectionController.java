package com.banklab.projections;

import com.corebanking.contracts.*;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
public class ProjectionController {
    private final ProjectionService service;
    public ProjectionController(ProjectionService service) { this.service = service; }
    @GetMapping("/lab/health") public String health() { return "UP"; }
    @GetMapping("/api/v1/consumers/{consumer}/events/{id}")
    public ProjectionService.ProjectionResult find(@PathVariable String consumer, @PathVariable UUID id) {
        return service.find(consumer, id);
    }
    @PostMapping("/api/v1/reconciliation/distributed")
    public ReconciliationReport reconcile(@RequestParam(defaultValue = "false") boolean repair) { return service.reconcile(repair); }
}
