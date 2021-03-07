package com.timjaanson.serialminder.errors.exceptions;

public class TraktAPIException extends RuntimeException {
    public TraktAPIException() {
    }

    public TraktAPIException(String message) {
        super(message);
    }

    public TraktAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public TraktAPIException(Throwable cause) {
        super(cause);
    }
}
