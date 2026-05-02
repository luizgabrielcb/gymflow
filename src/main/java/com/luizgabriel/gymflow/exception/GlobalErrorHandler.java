package com.luizgabriel.gymflow.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
                .message(e.getMessage())
                .status(e.getStatusCode().value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(defaultErrorMessage);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<DefaultErrorMessage> handleBadRequestException(BadRequestException e) {
        var defaultErrorMessage = DefaultErrorMessage.builder()
                .message(e.getMessage())
                .status(e.getStatusCode().value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(defaultErrorMessage);
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
