package com.skkutable.service;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.domain.PaymentMethod;
import com.skkutable.domain.Reservation;
import com.skkutable.domain.User;
import com.skkutable.dto.ReservationByBoothResponseDTO;
import com.skkutable.dto.ReservationRequestDTO;
import com.skkutable.dto.ReservationResponseDTO;
import com.skkutable.exception.BadRequestException;
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
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;
  private final BoothRepository boothRepository;
  private final FestivalRepository festivalRepository;

  public ReservationResponseDTO createReservation(ReservationRequestDTO dto) {
    if (dto == null) {
      throw new BadRequestException("Reservation data must be provided");
    }
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
    ReservationResponseDTO response = toResponseDTO(saved);

    // 🔔 Firestore에 예약 알림 정보 저장
    try {
      String fcmToken = dto.getFcmToken();                       // 1) dto에서 바로 꺼냅니다.
      if (fcmToken == null || fcmToken.isBlank()) {
        System.err.println("⚠️ reservationRequestDTO에 fcmToken이 없습니다.");
      } else {
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> alarmData = new HashMap<>();
        alarmData.put("userId",      dto.getUserId());
        alarmData.put("festivalName", response.getFestivalName());
        alarmData.put("boothName",    response.getBoothName());
        alarmData.put("reservationTime", response.getReservationTime().toString());
        alarmData.put("pushToken",      fcmToken);
        alarmData.put("notified",       false);
  
        db.collection("reservations").add(alarmData);
        System.out.println("✅ Firestore 예약 알림 정보 저장 완료");
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("❌ Firestore 알림 정보 저장 실패");
    }
    return response;
  }


  public List<ReservationResponseDTO> getReservationsByUser(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("User not found: " + userId);
    }
    return reservationRepository.findByUserIdWithBoothAndFestival(userId).stream()
        .map(this::toResponseDTO)
        .collect(Collectors.toList());
  }

  public ReservationByBoothResponseDTO getReservationsByFestivalAndBooth(Long festivalId, Long boothId) {
    if (festivalId == null || boothId == null) {
      throw new BadRequestException("Festival ID and Booth ID must be provided");
    }

    Booth booth = boothRepository.findById(boothId)
            .orElseThrow(() -> new ResourceNotFoundException("Booth not found: " + boothId));

    if (!booth.getFestival().getId().equals(festivalId)) {
      throw new ResourceNotFoundException("Booth does not belong to the given festival");
    }

    List<Reservation> reservations = reservationRepository.findByBoothFestivalIdAndBoothId(festivalId, boothId);

    ReservationByBoothResponseDTO response = new ReservationByBoothResponseDTO();

    ReservationByBoothResponseDTO.BoothInfo boothInfo = new ReservationByBoothResponseDTO.BoothInfo();
    boothInfo.setId(booth.getId());
    boothInfo.setName(booth.getName());
    boothInfo.setLocation(booth.getLocation());
    boothInfo.setStartTime(booth.getStartDateTime());
    boothInfo.setEndTime(booth.getEndDateTime());
    boothInfo.setPosterImageUrl(booth.getPosterImageUrl());
    boothInfo.setLikeCount(booth.getLikeCount());
    response.setBooth(boothInfo);

    List<ReservationByBoothResponseDTO.UserReservationInfo> userReservations = reservations.stream()
            .map(r -> {
              ReservationByBoothResponseDTO.UserReservationInfo info = new ReservationByBoothResponseDTO.UserReservationInfo();
              info.setReservationId(r.getId());
              info.setUserId(r.getUser().getId());
              info.setUserName(r.getUser().getName());
              info.setReservationTime(r.getReservationTime());
              info.setNumberOfPeople(r.getNumberOfPeople());
              info.setPaymentMethod(r.getPaymentMethod().name());
              info.setCreatedAt(r.getCreatedAt());
              return info;
            })
            .collect(Collectors.toList());

    response.setReservations(userReservations);
    return response;
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
    if (dto == null) {
      throw new BadRequestException("Patch data must be provided");
    }

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
