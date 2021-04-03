package com.timjaanson.serialminder.traktapi.exceptions;

public class TraktAPINotFoundException extends RuntimeException {
    public TraktAPINotFoundException() {
    }

    public TraktAPINotFoundException(String message) {
        super(message);
    }

    public TraktAPINotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TraktAPINotFoundException(Throwable cause) {
        super(cause);
    }
}
