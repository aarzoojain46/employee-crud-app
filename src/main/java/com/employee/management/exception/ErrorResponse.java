package com.employee.management.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        String path,
        Map<String, String> errors // field-level validation messages; null when not applicable
) {
    // Convenience constructor for errors without field-level details (e.g. not found)
    public ErrorResponse(LocalDateTime timestamp, int status, String message, String path) {
        this(timestamp, status, message, path, null);
    }
}