package com.jason.springbootmall.exception;

import com.jason.springbootmall.exception.ErrorResponse.FieldErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    // MethodArgumentNotValidException comes from @Valid request body validation.
    List<FieldErrorDetail> errors =
        exception.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
            .toList();

    ErrorResponse response =
        ErrorResponse.withErrors(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            "Request validation failed",
            request.getRequestURI(),
            errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatusException(
      ResponseStatusException exception, HttpServletRequest request) {
    // ResponseStatusException is used by Service/Controller for expected business errors.
    int status = exception.getStatusCode().value();
    String code = resolveStatusCode(status);
    String message = resolveMessage(exception, code);

    ErrorResponse response = ErrorResponse.of(status, code, message, request.getRequestURI());

    return ResponseEntity.status(status).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpectedException(
      Exception exception, HttpServletRequest request) {
    // Keep internal exception details in logs, not in the API response.
    log.error("Unexpected API error", exception);

    ErrorResponse response =
        ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_SERVER_ERROR",
            "Unexpected server error",
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  private String resolveStatusCode(int status) {
    HttpStatus httpStatus = HttpStatus.resolve(status);

    if (httpStatus == null) {
      return "HTTP_" + status;
    }

    return httpStatus.name();
  }

  private String resolveMessage(ResponseStatusException exception, String fallback) {
    if (exception.getReason() == null || exception.getReason().isBlank()) {
      return fallback;
    }

    return exception.getReason();
  }
}
