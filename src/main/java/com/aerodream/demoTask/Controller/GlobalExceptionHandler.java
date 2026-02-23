package com.aerodream.demoTask.Controller;

import com.aerodream.demoTask.Dto.Exception.ErrorResponse;
import com.aerodream.demoTask.Exception.DocumentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(DocumentNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                "DOCUMENT_NOT_FOUND",
                e.getMessage(),
                LocalDateTime.now()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "BAD_REQUEST",
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "INTERNAL_ERROR",
                        "An unexpected error occurred",
                        LocalDateTime.now()
                ));
    }
}
