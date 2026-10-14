package com.corebanking.contracts;

import java.util.List;

public record ReconciliationReport(String status, List<String> discrepancies, int repaired) {}
