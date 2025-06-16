package com.skkutable.controller;

import com.skkutable.dto.ReservationRequestDTO;
import com.skkutable.dto.ReservationResponseDTO;
import com.skkutable.service.ReservationServiceV2;
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
@RequestMapping("/v2/reservations")
@RequiredArgsConstructor
public class ReservationControllerV2 {

  private final ReservationServiceV2 reservationService;

  // 예약 생성 (USER/HOST/ADMIN)
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ReservationResponseDTO> createReservation(
      @Valid @RequestBody ReservationRequestDTO dto,
      @AuthenticationPrincipal(expression = "username") String email) {
    ReservationResponseDTO response = reservationService.createReservation(dto, email);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  //에약 수정 (USER/HOST/ADMIN - 본인 예약만)
  @PatchMapping("/{reservationId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ReservationResponseDTO> updateReservation(@PathVariable Long reservationId,
      @Valid @RequestBody ReservationRequestDTO dto,
      @AuthenticationPrincipal(expression = "username") String email) {
    ReservationResponseDTO response = reservationService.updateReservation(reservationId, dto,
        email);
    return ResponseEntity.ok(response);
  }

  // 예약 취소 (USER 본인 예약만)
  // HOST와 ADMIN은 본인이 만든 부스에 대해 예약 취소 권한이 있음
  @DeleteMapping("/{reservationId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId,
      @AuthenticationPrincipal(expression = "username") String email) {
    reservationService.cancelReservation(reservationId, email);
    return ResponseEntity.noContent().build();
  }

  // 내 예약 목록 조회 (USER/HOST/ADMIN)
  @GetMapping("/my")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<ReservationResponseDTO>> getMyReservations(
      @AuthenticationPrincipal(expression = "username") String email) {
    List<ReservationResponseDTO> reservations = reservationService.getUserReservations(email);
    return ResponseEntity.ok(reservations);
  }

  // 특정 타임슬롯의 예약 현황 조회 (HOST/ADMIN)
  @GetMapping("/timeslots/{timeSlotId}")
  @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
  public ResponseEntity<List<ReservationResponseDTO>> getTimeSlotReservations(
      @PathVariable Long timeSlotId,
      @AuthenticationPrincipal(expression = "username") String email) {
    List<ReservationResponseDTO> reservations = reservationService.getTimeSlotReservations(
        timeSlotId, email);
    return ResponseEntity.ok(reservations);
  }
}
