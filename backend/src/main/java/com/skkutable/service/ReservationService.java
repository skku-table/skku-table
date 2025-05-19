package com.skkutable.service;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Reservation;
import com.skkutable.domain.User;
import com.skkutable.dto.ReservationRequestDTO;
import com.skkutable.dto.ReservationResponseDTO;
import com.skkutable.repository.BoothRepository;
import com.skkutable.repository.ReservationRepository;
import com.skkutable.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BoothRepository boothRepository;

    public ReservationResponseDTO createReservation(ReservationRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booth booth = boothRepository.findById(dto.getBoothId())
                .orElseThrow(() -> new IllegalArgumentException("Booth not found"));

        Reservation reservation = new Reservation(
                null, user, booth, dto.getReservationTime(), dto.getNumberOfPeople(), null, null
        );

        Reservation saved = reservationRepository.save(reservation);
        return toResponseDTO(saved);
    }

    public List<ReservationResponseDTO> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDTO> getReservationsByBooth(Long boothId) {
        return reservationRepository.findByBoothId(boothId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private ReservationResponseDTO toResponseDTO(Reservation reservation) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setReservationId(reservation.getId());
        dto.setUserId(reservation.getUser().getId());
        dto.setUserName(reservation.getUser().getName());
        dto.setBoothId(reservation.getBooth().getId());
        dto.setBoothName(reservation.getBooth().getName());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setNumberOfPeople(reservation.getNumberOfPeople());
        dto.setCreatedAt(reservation.getCreatedAt());
        return dto;
    }
}
