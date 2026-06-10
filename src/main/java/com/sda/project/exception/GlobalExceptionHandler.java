package com.sda.project.exception;

import jakarta.validation.ValidationException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException ex) {
		return error(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Map<String, Object>> unauthorized(UnauthorizedException ex) {
		return error(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage());
	}

	@ExceptionHandler(SubscriptionRequiredException.class)
	public ResponseEntity<Map<String, Object>> subscriptionRequired(SubscriptionRequiredException ex) {
		return error(HttpStatus.PAYMENT_REQUIRED, "Subscription Required", ex.getMessage());
	}

	@ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class, IllegalArgumentException.class})
	public ResponseEntity<Map<String, Object>> badRequest(Exception ex) {
		return error(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> serverError(Exception ex) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", ex.getMessage());
	}

	private ResponseEntity<Map<String, Object>> error(HttpStatus status, String error, String message) {
		return ResponseEntity.status(status).body(Map.of(
				"error", error,
				"message", message == null ? status.getReasonPhrase() : message,
				"timestamp", Instant.now().toString()));
	}
}
