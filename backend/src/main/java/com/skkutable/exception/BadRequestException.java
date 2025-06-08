package com.skkutable.exception;

/**
 * 입력값이 비즈니스 규칙에 어긋날 때
 */
public class BadRequestException extends RuntimeException {

  public BadRequestException(String message) {
    super(message);
  }
}
