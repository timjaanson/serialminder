package com.timjaanson.serialminder.errors;

import com.timjaanson.serialminder.errors.exceptions.SeriesAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = { SeriesAlreadyExistsException.class })
    protected ResponseEntity<Object> handleSeriesAlreadyExists(
            RuntimeException ex, WebRequest request) {
        ErrorResponse res = new ErrorResponse(HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI());

        return new ResponseEntity<>(res, HttpStatus.CONFLICT);
    }

}