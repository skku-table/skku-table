package com.skkutable.exception;

/**
 * 존재하지 않는 리소스를 요청했을 때
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}

