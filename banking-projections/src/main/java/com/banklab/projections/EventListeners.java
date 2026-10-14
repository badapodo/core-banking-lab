package com.banklab.projections;

import com.corebanking.contracts.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventListeners {
    private final ProjectionService service;
    private final ObjectMapper mapper;
    public EventListeners(ProjectionService service, ObjectMapper mapper) { this.service = service; this.mapper = mapper; }
    @KafkaListener(id = "audit", topics = "${lab.events-topic:transfer-committed}", groupId = "${lab.audit-group:audit}")
    public void audit(String json) throws Exception { service.consume("audit", mapper.readValue(json, TransferCommitted.class)); }
    @KafkaListener(id = "notifications", topics = "${lab.events-topic:transfer-committed}", groupId = "${lab.notification-group:notifications}")
    public void notifications(String json) throws Exception { service.consume("notifications", mapper.readValue(json, TransferCommitted.class)); }
}
