package com.luizgabriel.gymflow.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Log4j2
public class GlobalErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var errorsMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.joining(", "));

        var defaultErrorMessage = DefaultErrorMessage.builder()
                .message(errorsMessage)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(defaultErrorMessage);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<DefaultErrorMessage> handleNotFoundException(NotFoundException e) {
        var defaultErrorMessage = DefaultErrorMessage.builder()
                .message(e.getReason())
                .status(e.getStatusCode().value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(defaultErrorMessage);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<DefaultErrorMessage> handleBadRequestException(BadRequestException e) {
        var defaultErrorMessage = DefaultErrorMessage.builder()
                .message(e.getReason())
                .status(e.getStatusCode().value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(defaultErrorMessage);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<DefaultErrorMessage> handleForbiddenException(ForbiddenException e) {
        var defaultErrorMessage = DefaultErrorMessage.builder()
                .message(e.getReason())
                .status(e.getStatusCode().value())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(defaultErrorMessage);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<DefaultErrorMessage> handleUnauthorizedException(UnauthorizedException e) {
        var defaultErrorMessage = DefaultErrorMessage.builder()
                .message(e.getReason())
                .status(e.getStatusCode().value())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(defaultErrorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DefaultErrorMessage> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        var defaultErrorMessage = DefaultErrorMessage.builder()
                .message("Malformed or unreadable request body.")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(defaultErrorMessage);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DefaultErrorMessage> handleBadCredentialsException(BadCredentialsException e) {
        var defaultErrorMessage = DefaultErrorMessage.builder()
                .message(e.getMessage())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(defaultErrorMessage);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DefaultErrorMessage> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        var defaultErrorMessage = DefaultErrorMessage.builder()
                .message("Cannot delete this resource because it is being used by other records")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(defaultErrorMessage);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DefaultErrorMessage> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e) {
        var defaultErrorMessage = DefaultErrorMessage.builder()
                .message("HTTP method not allowed.")
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(defaultErrorMessage);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<DefaultErrorMessage> handleNoResourceFoundException(NoResourceFoundException e) {
        var defaultErrorMessage = DefaultErrorMessage.builder()
                .message("Resource not found.")
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(defaultErrorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultErrorMessage> handleException(Exception e) {
        log.error("Unexpected error: ", e);

        var defaultErrorMessage = DefaultErrorMessage.builder()
                .message("An unexpected error occurred. Please try again later.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(defaultErrorMessage);
    }
}
