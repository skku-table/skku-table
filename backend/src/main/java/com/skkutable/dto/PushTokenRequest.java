package com.skkutable.dto;

public class PushTokenRequest {

  private Long userId;
  private String fcmToken;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getFcmToken() {
    return fcmToken;
  }

  public void setFcmToken(String fcmToken) {
    this.fcmToken = fcmToken;
  }
}
