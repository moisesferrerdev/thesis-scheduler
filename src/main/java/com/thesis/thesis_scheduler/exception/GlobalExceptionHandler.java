
package com.thesis.thesis_scheduler.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "Invalid value"
                )
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request validation failed.",
                request.getRequestURI(),
                errors
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getConstraintViolations().forEach(violation ->
                errors.putIfAbsent(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                )
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request constraint validation failed.",
                request.getRequestURI(),
                errors
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage() != null
                        ? ex.getMessage()
                        : "Invalid request.",
                request.getRequestURI(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.warn(
                "Database integrity violation at {}",
                request.getRequestURI(),
                ex
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                "The request conflicts with existing data or database constraints.",
                request.getRequestURI(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error at {}", request.getRequestURI(), ex);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred.",
                request.getRequestURI(),
                Collections.emptyMap()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> validationErrors) {

        ApiErrorResponse response = new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                validationErrors
        );

        return ResponseEntity.status(status).body(response);
    }
}
