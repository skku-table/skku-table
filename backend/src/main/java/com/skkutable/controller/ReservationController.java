package com.skkutable.controller;

import com.skkutable.dto.ReservationRequestDTO;
import com.skkutable.dto.ReservationResponseDTO;
import com.skkutable.service.ReservationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @PostMapping
  public ResponseEntity<ReservationResponseDTO> createReservation(
      @RequestBody ReservationRequestDTO dto) {
    ReservationResponseDTO created = reservationService.createReservation(dto);
    return ResponseEntity.ok(created);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<ReservationResponseDTO>> getReservationsByUser(
      @PathVariable Long userId) {
    return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
  }

  @GetMapping("/festival/{festivalId}/booth/{boothId}")
  public ResponseEntity<List<ReservationResponseDTO>> getReservationsByFestivalAndBooth(
      @PathVariable Long festivalId,
      @PathVariable Long boothId) {
    return ResponseEntity.ok(
        reservationService.getReservationsByFestivalAndBooth(festivalId, boothId));
  }

  @GetMapping("/{reservationId}")
  public ResponseEntity<ReservationResponseDTO> getReservationById(
      @PathVariable Long reservationId) {
    return ResponseEntity.ok(reservationService.getReservationById(reservationId));
  }

  @PatchMapping("/{reservationId}")
  public ResponseEntity<ReservationResponseDTO> patchReservation(
      @PathVariable Long reservationId,
      @RequestBody ReservationRequestDTO dto) {
    ReservationResponseDTO updated = reservationService.patchReservation(reservationId, dto);
    return ResponseEntity.ok(updated);
  }

  @PutMapping("/{reservationId}")
  public ResponseEntity<ReservationResponseDTO> updateReservation(
      @PathVariable Long reservationId,
      @RequestBody ReservationRequestDTO dto
  ) {
    ReservationResponseDTO updated = reservationService.updateReservation(reservationId, dto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{reservationId}")
  public ResponseEntity<Void> deleteReservation(@PathVariable Long reservationId) {
    reservationService.deleteReservation(reservationId);
    return ResponseEntity.noContent().build(); // 204 No Content
  }
}
