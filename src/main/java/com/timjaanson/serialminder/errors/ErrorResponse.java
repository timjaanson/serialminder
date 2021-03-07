package com.timjaanson.serialminder.errors;

import java.time.Instant;

public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    private ErrorResponse() {
        this.timestamp = Instant.now();
    }

    public ErrorResponse(int status, String error) {
        this();
        this.status = status;
        this.error = error;
    }

    public ErrorResponse(int status, String error, String path ) {
        this();
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

}
