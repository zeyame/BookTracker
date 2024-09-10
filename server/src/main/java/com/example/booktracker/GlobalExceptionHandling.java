package com.example.booktracker;

import com.example.booktracker.book.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;

@RestControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFoundException (BookNotFoundException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException (ExternalServiceException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException (AuthenticationException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException (CustomBadRequestException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }


    @ExceptionHandler(GenreNotInCacheException.class)
    public ResponseEntity<ErrorResponse> handleGenreNotInCacheException (GenreNotInCacheException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException (RuntimeException exception) {
        String message = "An unexpected error occurred.";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }
}
