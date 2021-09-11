package com.timjaanson.serialminder.util;

import lombok.Data;

import java.time.Instant;

@Data
public class ErrorResponse {
    private Instant timestamp = Instant.now();
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(int status, String error) {
        this.status = status;
        this.error = error;
    }

    public ErrorResponse(int status, String error, String path ) {
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
