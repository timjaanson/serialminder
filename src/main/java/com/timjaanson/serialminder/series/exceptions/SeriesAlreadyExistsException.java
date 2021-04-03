package com.timjaanson.serialminder.series.exceptions;

public class SeriesAlreadyExistsException extends RuntimeException {
    public SeriesAlreadyExistsException() {
    }

    public SeriesAlreadyExistsException(String message) {
        super(message);
    }

    public SeriesAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeriesAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
