package com.banklab.orchestrator;

import com.corebanking.contracts.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpCoreBankingAdapter implements CoreBankingPort {
    private final String baseUrl;
    public HttpCoreBankingAdapter(@Value("${lab.core-url:http://localhost:8080}") String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public TransferResult transfer(TransferCommand command, String key) {
        throw new UnsupportedOperationException("TODO: A06 HTTP call to " + baseUrl);
    }
    public TransferResult findByKey(String key) { throw new UnsupportedOperationException("TODO: A07 result resolution"); }
}
