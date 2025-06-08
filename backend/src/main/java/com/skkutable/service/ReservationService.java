package com.skkutable.service;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.domain.PaymentMethod;
import com.skkutable.domain.Reservation;
import com.skkutable.domain.User;
import com.skkutable.dto.ReservationRequestDTO;
import com.skkutable.dto.ReservationResponseDTO;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.BoothRepository;
import com.skkutable.repository.FestivalRepository;
import com.skkutable.repository.ReservationRepository;
import com.skkutable.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;
  private final BoothRepository boothRepository;
  private final FestivalRepository festivalRepository;

  public ReservationResponseDTO createReservation(ReservationRequestDTO dto) {
    User user = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));

    Booth booth = boothRepository.findById(dto.getBoothId())
        .orElseThrow(() -> new ResourceNotFoundException("Booth not found: " + dto.getBoothId()));

    Festival festival = festivalRepository.findById(dto.getFestivalId())
        .orElseThrow(
            () -> new ResourceNotFoundException("Festival not found: " + dto.getFestivalId()));

    PaymentMethod paymentMethod;

    try {
      paymentMethod = PaymentMethod.valueOf(dto.getPaymentMethod().toUpperCase());
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new IllegalArgumentException("Invalid payment method: " + dto.getPaymentMethod());
    }

    Reservation reservation = new Reservation(
        user,
        booth,
        festival,
        dto.getReservationTime(),
        dto.getNumberOfPeople()
    );
    reservation.setPaymentMethod(paymentMethod);

    Reservation saved = reservationRepository.save(reservation);
    return toResponseDTO(saved);
  }


  public List<ReservationResponseDTO> getReservationsByUser(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("User not found: " + userId);
    }
    return reservationRepository.findByUserIdWithBoothAndFestival(userId).stream()
        .map(this::toResponseDTO)
        .collect(Collectors.toList());
  }

  public List<ReservationResponseDTO> getReservationsByFestivalAndBooth(Long festivalId,
      Long boothId) {
    if (!festivalRepository.existsById(festivalId)) {
      throw new ResourceNotFoundException("Festival not found: " + festivalId);
    }
    if (!boothRepository.existsById(boothId)) {
      throw new ResourceNotFoundException("Booth not found: " + boothId);
    }
    List<Reservation> reservations = reservationRepository.findByBoothFestivalIdAndBoothId(
        festivalId, boothId);
    return reservations.stream()
        .map(this::toResponseDTO)
        .toList();
  }

  public ReservationResponseDTO updateReservation(Long reservationId, ReservationRequestDTO dto) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Reservation not found: " + reservationId));

    User user = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));
    Booth booth = boothRepository.findById(dto.getBoothId())
        .orElseThrow(() -> new ResourceNotFoundException("Booth not found: " + dto.getBoothId()));

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

  public ReservationResponseDTO getReservationById(Long id) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    return toResponseDTO(reservation);
  }

  public ReservationResponseDTO patchReservation(Long id, ReservationRequestDTO dto) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

    if (dto.getBoothId() != null) {
      Booth booth = boothRepository.findById(dto.getBoothId())
          .orElseThrow(() -> new ResourceNotFoundException("Booth not found"));
      reservation.setBooth(booth);
    }

    if (dto.getReservationTime() != null) {
      reservation.setReservationTime(dto.getReservationTime());
    }

    if (dto.getNumberOfPeople() != null) {
      reservation.setNumberOfPeople(dto.getNumberOfPeople());
    } else {
      throw new IllegalArgumentException("Number of people must not be null");
    }

    if (dto.getPaymentMethod() != null) {
      try {
        reservation.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod().toUpperCase()));
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid payment method: " + dto.getPaymentMethod());
      }
    }

    return toResponseDTO(reservationRepository.save(reservation));
  }


  private ReservationResponseDTO toResponseDTO(Reservation reservation) {
    ReservationResponseDTO dto = new ReservationResponseDTO();
    dto.setReservationId(reservation.getId());
    dto.setUserId(reservation.getUser().getId());
    dto.setUserName(reservation.getUser().getName());
    dto.setBoothId(reservation.getBooth().getId());
    dto.setBoothName(reservation.getBooth().getName());
    dto.setFestivalId(reservation.getBooth().getFestival().getId());
    dto.setFestivalName(reservation.getBooth().getFestival().getName());
    dto.setBoothStartDate(reservation.getBooth().getStartDateTime());
    dto.setBoothPosterImageUrl(reservation.getBooth().getPosterImageUrl());
    dto.setReservationTime(reservation.getReservationTime());
    dto.setNumberOfPeople(reservation.getNumberOfPeople());
    dto.setPaymentMethod(reservation.getPaymentMethod().name());
    dto.setCreatedAt(reservation.getCreatedAt());
    return dto;
  }
}
