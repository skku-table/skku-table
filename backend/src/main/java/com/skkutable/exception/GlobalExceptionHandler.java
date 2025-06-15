package com.skkutable.exception;

import com.skkutable.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private ResponseEntity<ApiError> buildErrorResponse(
      HttpStatus status, String message, HttpServletRequest req) {

    ApiError body = new ApiError(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        req.getRequestURI()
    );
    return ResponseEntity.status(status).body(body);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex,
      HttpServletRequest req) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), req);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex,
      HttpServletRequest req) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest req) {
    return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), req);
  }

  @ExceptionHandler(ForbiddenOperationException.class)
  public ResponseEntity<ApiError> handleForbidden(ForbiddenOperationException ex,
      HttpServletRequest req) {
    return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), req);
  }

  @ExceptionHandler(UnauthorizedAccessException.class)
  public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedAccessException ex,
      HttpServletRequest req) {
    return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), req);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex,
      HttpServletRequest req) {
    return buildErrorResponse(HttpStatus.CONFLICT, "데이터 무결성 제약 조건 위반", req);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
      HttpServletRequest req) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .findFirst()
        .orElse("유효성 검증 실패");
    return buildErrorResponse(HttpStatus.BAD_REQUEST, message, req);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex,
      HttpServletRequest req) {
    return buildErrorResponse(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", req);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
      HttpServletRequest req) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다: " + ex.getMessage(), req);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ApiError> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex,
      HttpServletRequest req) {
    return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), req);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiError> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpServletRequest req) {
    return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), req);
  }

  /**
   * 예상치 못한 런타임 예외
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneral(Exception ex, HttpServletRequest req) {
    log.error("Unhandled exception", ex);
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req);
  }
}
