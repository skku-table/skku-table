package com.skkutable.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {
  private Instant timestamp;
  private int      status;      // 404
  private String   error;       // "Not Found"
  private String   message;     // "Festival not found: 99"
  private String   path;        // "/festivals/99"
}