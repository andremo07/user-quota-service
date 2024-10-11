package com.vicarius.quota.exception.handler;

import com.vicarius.quota.dto.Error;
import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.exception.UserBlockedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final String UNMAPPED_ERROR_MESSAGE = "Unmapped Error";

    @ExceptionHandler({ResourceNotFoundException.class})
    private ResponseEntity<Error> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<Error> handleUserBlockedException(UserBlockedException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(error);
    }

    @ExceptionHandler({Exception.class})
    private ResponseEntity<Error> handleException(Exception ex) {
        Error error = new Error();
        error.setMessage(UNMAPPED_ERROR_MESSAGE);
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
