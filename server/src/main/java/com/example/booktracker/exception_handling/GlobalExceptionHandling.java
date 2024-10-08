package com.example.booktracker.exception_handling;

import com.example.booktracker.book.exception.*;
import com.example.booktracker.otp.exception.IncorrectOtpException;
import com.example.booktracker.otp.exception.InvalidOtpException;
import com.example.booktracker.otp.exception.OtpAlreadySentException;
import com.example.booktracker.user.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;

@RestControllerAdvice
public class GlobalExceptionHandling {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException (RuntimeException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

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


    // user registration and login related exceptions
    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyRegisteredException(EmailAlreadyRegisteredException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(UsernameAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyRegisteredException(UsernameAlreadyRegisteredException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernammeNotFoundException(UsernameNotFoundException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotVerifiedException(UserNotVerifiedException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyVerifiedException(UserAlreadyVerifiedException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.OK;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<ErrorResponse> handleMailSendingException(MailException exception) {
        String message = "Failed to send verification email.";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }


    // otp exceptions
    @ExceptionHandler(OtpAlreadySentException.class)
    public ResponseEntity<ErrorResponse> handleOtpAlreadySentException(OtpAlreadySentException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.OK;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(IncorrectOtpException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectOtpException(IncorrectOtpException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOtpException(InvalidOtpException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        return new ResponseEntity<>(errorResponse, status);
    }

}
