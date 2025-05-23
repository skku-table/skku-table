package com.skkutable.service;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Reservation;
import com.skkutable.domain.User;
import com.skkutable.dto.ReservationRequestDTO;
import com.skkutable.dto.ReservationResponseDTO;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.BoothRepository;
import com.skkutable.repository.ReservationRepository;
import com.skkutable.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));

        Booth booth = boothRepository.findById(dto.getBoothId())
                .orElseThrow(() -> new ResourceNotFoundException("Booth not found: " + dto.getBoothId()));

        Reservation reservation = new Reservation(
                null, user, booth, dto.getReservationTime(), dto.getNumberOfPeople(), null, null
        );

        Reservation saved = reservationRepository.save(reservation);
        return toResponseDTO(saved);
    }

    public List<ReservationResponseDTO> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserIdWithBoothAndFestival(userId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDTO> getReservationsByFestivalAndBooth(Long festivalId, Long boothId) {
        List<Reservation> reservations = reservationRepository.findByBoothFestivalIdAndBoothId(festivalId, boothId);
        return reservations.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ReservationResponseDTO updateReservation(Long reservationId, ReservationRequestDTO dto) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: "+ reservationId));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: "+ dto.getUserId()));
        Booth booth = boothRepository.findById(dto.getBoothId())
                .orElseThrow(() -> new ResourceNotFoundException("Booth not found: "+ dto.getBoothId()));

        reservation.setUser(user);
        reservation.setBooth(booth);
        reservation.setReservationTime(dto.getReservationTime());
        reservation.setNumberOfPeople(dto.getNumberOfPeople());

        Reservation saved = reservationRepository.save(reservation);
        return toResponseDTO(saved);
    }

    public void deleteReservation(Long reservationId) {
        try {
            reservationRepository.deleteById(reservationId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Reservation not found: " + reservationId);
        }
    }

    private ReservationResponseDTO toResponseDTO(Reservation reservation) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setReservationId(reservation.getId());
        dto.setUserId(reservation.getUser().getId());
        dto.setUserName(reservation.getUser().getName());
        dto.setBoothId(reservation.getBooth().getId());
        dto.setBoothName(reservation.getBooth().getName());
        dto.setFestivalName(reservation.getBooth().getFestival().getName());
        dto.setBoothStartDate(reservation.getBooth().getStartDateTime());
        dto.setBoothPosterImageUrl(reservation.getBooth().getPosterImageUrl());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setNumberOfPeople(reservation.getNumberOfPeople());
        dto.setCreatedAt(reservation.getCreatedAt());
        return dto;
    }
}
