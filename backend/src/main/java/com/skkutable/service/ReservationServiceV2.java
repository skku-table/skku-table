package com.skkutable.service;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.domain.PaymentMethod;
import com.skkutable.domain.Reservation;
import com.skkutable.domain.TimeSlot;
import com.skkutable.domain.User;
import com.skkutable.dto.ReservationRequestDTO;
import com.skkutable.dto.ReservationResponseDTO;
import com.skkutable.exception.BadRequestException;
import com.skkutable.exception.ConflictException;
import com.skkutable.exception.ForbiddenOperationException;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.BoothRepository;
import com.skkutable.repository.FestivalRepository;
import com.skkutable.repository.ReservationRepository;
import com.skkutable.repository.TimeSlotRepository;
import com.skkutable.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationServiceV2 {

  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;
  private final BoothRepository boothRepository;
  private final FestivalRepository festivalRepository;
  private final TimeSlotRepository timeSlotRepository;

  // 예약 생성 (동시성 제어 적용)
  public ReservationResponseDTO createReservation(ReservationRequestDTO dto, String userEmail) {
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

    // 요청한 유저와 예약 유저가 같은지 확인
    if (!user.getId().equals(dto.getUserId())) {
      throw new ForbiddenOperationException("본인의 예약만 생성할 수 있습니다");
    }

    Booth booth = boothRepository.findById(dto.getBoothId())
        .orElseThrow(() -> new ResourceNotFoundException("Booth not found: " + dto.getBoothId()));

    Festival festival = festivalRepository.findById(dto.getFestivalId()).orElseThrow(
        () -> new ResourceNotFoundException("Festival not found: " + dto.getFestivalId()));

    // ⭐ 비관적 락을 사용하여 타임슬롯 조회 (동시성 제어)
    TimeSlot timeSlot = timeSlotRepository.findByIdWithLock(dto.getTimeSlotId()).orElseThrow(
        () -> new ResourceNotFoundException("TimeSlot not found: " + dto.getTimeSlotId()));

    // 예약 가능 시간 확인
    if (booth.getReservationOpenTime() != null && LocalDateTime.now()
        .isBefore(booth.getReservationOpenTime())) {
      throw new BadRequestException("예약 가능 시간이 아닙니다. 예약 오픈 시간: " + booth.getReservationOpenTime());
    }

    // TimeSlot 유효성 검증
    if (!timeSlot.getBooth().getId().equals(booth.getId())) {
      throw new BadRequestException("타임슬롯이 해당 부스에 속하지 않습니다");
    }

    // ⭐ 락이 적용된 상태에서 수용 인원 재검증
    if (!timeSlot.canAccommodate(dto.getNumberOfPeople())) {
      throw new ConflictException(
          "남은 자리가 부족합니다. 현재 가능 인원: " + (timeSlot.getMaxCapacity() - timeSlot.getCurrentCapacity()));
    }

    // 중복 예약 검증
    boolean alreadyReserved = reservationRepository.existsByUserIdAndTimeSlotId(user.getId(),
        timeSlot.getId());
    if (alreadyReserved) {
      throw new BadRequestException("이미 해당 타임슬롯에 예약이 존재합니다");
    }

    if (!timeSlot.isAvailable()) {
      throw new BadRequestException("예약이 불가능한 타임슬롯입니다");
    }

    PaymentMethod paymentMethod;
    try {
      paymentMethod = PaymentMethod.valueOf(dto.getPaymentMethod().toUpperCase());
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new IllegalArgumentException("Invalid payment method: " + dto.getPaymentMethod());
    }

    Reservation reservation = new Reservation(user, booth, festival,
        dto.getNumberOfPeople());
    reservation.setPaymentMethod(paymentMethod);
    reservation.setTimeSlot(timeSlot);

    // ⭐ 락이 적용된 상태에서 타임슬롯 수용 인원 업데이트 (원자적 연산 보장)
    for (int i = 0; i < dto.getNumberOfPeople(); i++) {
      timeSlot.incrementCapacity();
    }
    timeSlotRepository.save(timeSlot);

    Reservation saved = reservationRepository.save(reservation);
    return toResponseDTO(saved);
  }

  // 예약 취소 (동시성 제어 적용)
  public void cancelReservation(Long reservationId, String userEmail) {
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
        () -> new ResourceNotFoundException("Reservation not found: " + reservationId));

    // 본인 예약인지 확인
    if (!reservation.getUser().getId().equals(user.getId())) {
      throw new ForbiddenOperationException("본인의 예약만 취소할 수 있습니다");
    }

    // ⭐ 비관적 락을 사용하여 타임슬롯 조회 후 수용 인원 감소
    if (reservation.getTimeSlot() != null) {
      TimeSlot timeSlot = timeSlotRepository.findByIdWithLock(reservation.getTimeSlot().getId())
          .orElse(null);

      if (timeSlot != null) {
        for (int i = 0; i < reservation.getNumberOfPeople(); i++) {
          timeSlot.decrementCapacity();
        }
        timeSlotRepository.save(timeSlot);
      }
    }

    reservationRepository.delete(reservation);
  }

  // 사용자의 예약 목록 조회
  @Transactional(readOnly = true)
  public List<ReservationResponseDTO> getUserReservations(String userEmail) {
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

    List<Reservation> reservations = reservationRepository.findByUserIdWithTimeSlot(user.getId());
    return reservations.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  // 특정 부스의 특정 타임슬롯 예약 현황 조회 (HOST/ADMIN)
  @Transactional(readOnly = true)
  public List<ReservationResponseDTO> getTimeSlotReservations(Long timeSlotId, String userEmail) {
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

    TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
        .orElseThrow(() -> new ResourceNotFoundException("TimeSlot not found: " + timeSlotId));

    Booth booth = timeSlot.getBooth();

    // 권한 확인: HOST는 자신의 부스만, ADMIN은 모든 부스
    if (user.getRole() == com.skkutable.domain.Role.HOST && !booth.getCreatedBy().getId()
        .equals(user.getId())) {
      throw new ForbiddenOperationException("자신의 부스 예약만 조회할 수 있습니다");
    }

    if (user.getRole() == com.skkutable.domain.Role.USER) {
      throw new ForbiddenOperationException("일반 사용자는 예약 현황을 조회할 수 없습니다");
    }

    List<Reservation> reservations = reservationRepository.findByTimeSlotId(timeSlotId);
    return reservations.stream().map(this::toResponseDTO).collect(Collectors.toList());
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
    dto.setNumberOfPeople(reservation.getNumberOfPeople());
    dto.setPaymentMethod(reservation.getPaymentMethod().name());
    dto.setCreatedAt(reservation.getCreatedAt());

    // TimeSlot 정보 추가
    if (reservation.getTimeSlot() != null) {
      dto.setTimeSlotId(reservation.getTimeSlot().getId());
      dto.setTimeSlotStartTime(reservation.getTimeSlot().getStartTime());
      dto.setTimeSlotEndTime(reservation.getTimeSlot().getEndTime());
    }

    return dto;
  }
}
