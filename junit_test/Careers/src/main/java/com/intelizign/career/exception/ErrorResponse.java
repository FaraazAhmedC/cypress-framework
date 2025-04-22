package com.intelizign.career.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ErrorResponse {
    private final int status;
    private final String message;
    private final String errorDetails; // Renamed for clarity
    private final HttpStatus httpStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    public ErrorResponse(HttpStatus httpStatus, String message, Throwable throwable) {
        this.status = httpStatus.value();
        this.httpStatus = httpStatus;
        this.message = message;
        this.errorDetails = (throwable != null) ? throwable.getMessage() : "No additional error details"; 
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}