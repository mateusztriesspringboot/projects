package com.javaproject.projects.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Global Exception Handler for MethodArgumentNotValidException thrown during input validation. Specific validation
 * errors for input parameters that failed validation are included in the error message response.
 */
@ControllerAdvice
public class BadRequestExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, Object> errors = new LinkedHashMap<>();
    StringBuilder errorMessage = new StringBuilder();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
          errorMessage.append(error.getField());
          errorMessage.append(": ");
          errorMessage.append(error.getDefaultMessage());
          errorMessage.append(". ");
        }
    );
    errors.put("timestamp", new Date());
    errors.put("status", 400);
    errors.put("error", "Bad Request");
    errors.put("message", errorMessage.toString());
    errors.put("path", request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }
}
