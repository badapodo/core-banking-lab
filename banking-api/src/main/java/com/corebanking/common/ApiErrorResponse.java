package com.corebanking.common;

public record ApiErrorResponse(
        String code,
        String message
) {
}
