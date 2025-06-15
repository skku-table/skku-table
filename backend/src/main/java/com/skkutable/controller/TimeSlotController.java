package com.skkutable.controller;

import com.skkutable.dto.TimeSlotCreateDto;
import com.skkutable.dto.TimeSlotResponseDto;
import com.skkutable.dto.TimeSlotUpdateDto;
import com.skkutable.service.TimeSlotService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booths/{boothId}/timeslots")
@RequiredArgsConstructor
public class TimeSlotController {

  private final TimeSlotService timeSlotService;

  // TimeSlot 생성 (HOST/ADMIN)
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
  public ResponseEntity<TimeSlotResponseDto> createTimeSlot(@PathVariable Long boothId,
      @Valid @RequestBody TimeSlotCreateDto dto,
      @AuthenticationPrincipal(expression = "username") String email) {
    TimeSlotResponseDto response = timeSlotService.createTimeSlot(boothId, dto, email);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // TimeSlot 수정 (HOST/ADMIN)
  @PatchMapping("/{timeSlotId}")
  @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
  public ResponseEntity<TimeSlotResponseDto> updateTimeSlot(@PathVariable Long boothId,
      @PathVariable Long timeSlotId, @Valid @RequestBody TimeSlotUpdateDto dto,
      @AuthenticationPrincipal(expression = "username") String email) {
    TimeSlotResponseDto response = timeSlotService.updateTimeSlot(boothId, timeSlotId, dto, email);
    return ResponseEntity.ok(response);
  }

  // TimeSlot 삭제 (HOST/ADMIN)
  @DeleteMapping("/{timeSlotId}")
  @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
  public ResponseEntity<Void> deleteTimeSlot(@PathVariable Long boothId,
      @PathVariable Long timeSlotId,
      @AuthenticationPrincipal(expression = "username") String email) {
    timeSlotService.deleteTimeSlot(boothId, timeSlotId, email);
    return ResponseEntity.noContent().build();
  }

  // 부스의 모든 TimeSlot 조회 (모든 사용자)
  @GetMapping
  public ResponseEntity<List<TimeSlotResponseDto>> getTimeSlots(@PathVariable Long boothId) {
    List<TimeSlotResponseDto> timeSlots = timeSlotService.getTimeSlotsByBooth(boothId);
    return ResponseEntity.ok(timeSlots);
  }

  // 부스의 예약 가능한 TimeSlot 조회 (모든 사용자)
  @GetMapping("/available")
  public ResponseEntity<List<TimeSlotResponseDto>> getAvailableTimeSlots(
      @PathVariable Long boothId) {
    List<TimeSlotResponseDto> timeSlots = timeSlotService.getAvailableTimeSlots(boothId);
    return ResponseEntity.ok(timeSlots);
  }
}
