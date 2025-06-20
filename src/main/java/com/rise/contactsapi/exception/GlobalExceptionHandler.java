package com.rise.contactsapi.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException ex) {
    log.warn("NotFoundException: {}", ex.getMessage());
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
    List<String> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .collect(Collectors.toList());

    log.warn("Validation failed: {}", errors);
    return ResponseEntity.badRequest().body(Map.of("errors", errors));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleInvalidJson(HttpMessageNotReadableException ex) {
    Throwable cause = ex.getCause();

    if (cause instanceof UnrecognizedPropertyException unrecognized) {
      log.warn("Unknown JSON property: {}", unrecognized.getPropertyName());
      return ResponseEntity.badRequest().body(Map.of("error", "Unknown property: " + unrecognized.getPropertyName()));
    }

    log.warn("Invalid JSON format: {}", ex.getMessage());
    return ResponseEntity.badRequest()
        .body(Map.of("error", "Invalid JSON format: check for trailing commas or syntax errors"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    Map<String, String> error = new HashMap<>();
    error.put("error", "An unexpected error occurred");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}