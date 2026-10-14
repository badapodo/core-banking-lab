package com.banklab.projections;

import com.corebanking.contracts.LabException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class ErrorAdvice {
    @ExceptionHandler({org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class,
            org.springframework.web.bind.MissingRequestHeaderException.class})
    public ResponseEntity<Map<String, String>> invalid(Exception error) {
        return ResponseEntity.badRequest().body(Map.of("code", "INVALID_REQUEST", "message", "Invalid request."));
    }
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Map<String, String>> todo(UnsupportedOperationException error) {
        return ResponseEntity.status(501).body(Map.of("code", "TODO", "message", error.getMessage()));
    }
    @ExceptionHandler(LabException.class)
    public ResponseEntity<Map<String, String>> business(LabException error) {
        return ResponseEntity.status(error.status()).body(Map.of("code", error.code(), "message", error.getMessage()));
    }
}
