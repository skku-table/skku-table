package com.skkutable.controller;

import com.skkutable.dto.ReservationRequestDTO;
import com.skkutable.dto.ReservationResponseDTO;
import com.skkutable.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(@RequestBody ReservationRequestDTO dto) {
        ReservationResponseDTO created = reservationService.createReservation(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }

    @GetMapping("/booth/{boothId}")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByBooth(@PathVariable Long boothId) {
        return ResponseEntity.ok(reservationService.getReservationsByBooth(boothId));
    }
}
