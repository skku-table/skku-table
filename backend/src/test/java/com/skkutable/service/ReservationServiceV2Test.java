package com.skkutable.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.domain.PaymentMethod;
import com.skkutable.domain.Reservation;
import com.skkutable.domain.Role;
import com.skkutable.domain.TimeSlot;
import com.skkutable.domain.TimeSlotStatus;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceV2Test {

  @Mock
  private ReservationRepository reservationRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BoothRepository boothRepository;

  @Mock
  private FestivalRepository festivalRepository;

  @Mock
  private TimeSlotRepository timeSlotRepository;

  @InjectMocks
  private ReservationServiceV2 reservationService;

  private User user;
  private User hostUser;
  private Booth booth;
  private Festival festival;
  private TimeSlot timeSlot;
  private Reservation reservation;
  private ReservationRequestDTO requestDTO;

  @BeforeEach
  void setUp() {
    user = User.builder().id(1L).name("Test User").email("user@test.com").role(Role.USER).build();

    hostUser = User.builder().id(2L).name("Host User").email("host@test.com").role(Role.HOST)
        .build();

    festival = Festival.builder().id(1L).name("Test Festival").build();

    booth = Booth.builder().id(1L).name("Test Booth")
        .reservationOpenTime(LocalDateTime.now().minusHours(1)) // 예약 가능 시간
        .build();
    booth.setFestival(festival);
    booth.setCreatedBy(hostUser);

    timeSlot = TimeSlot.builder().id(1L).booth(booth).startTime(LocalDateTime.now().plusHours(1))
        .endTime(LocalDateTime.now().plusHours(2)).maxCapacity(10).currentCapacity(3)
        .status(TimeSlotStatus.AVAILABLE).build();

    reservation = new Reservation(user, booth, festival, 3);
    reservation.setId(1L);
    reservation.setTimeSlot(timeSlot);
    reservation.setPaymentMethod(PaymentMethod.CARD);

    requestDTO = new ReservationRequestDTO();
    requestDTO.setUserId(1L);
    requestDTO.setBoothId(1L);
    requestDTO.setFestivalId(1L);
    requestDTO.setTimeSlotId(1L);
    requestDTO.setNumberOfPeople(3);
    requestDTO.setPaymentMethod("CARD");
  }

  @Test
  @DisplayName("예약을 성공적으로 생성한다")
  void createReservation_Success() {
    // given
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
    when(festivalRepository.findById(1L)).thenReturn(Optional.of(festival));
    when(timeSlotRepository.findByIdWithLock(1L)).thenReturn(Optional.of(timeSlot));
    when(reservationRepository.existsByUserIdAndTimeSlotId(1L, 1L)).thenReturn(false);
    when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

    // when
    ReservationResponseDTO result = reservationService.createReservation(requestDTO,
        "user@test.com");

    // then
    assertNotNull(result);
    assertEquals(1L, result.getReservationId());
    assertEquals(1L, result.getUserId());
    assertEquals("Test User", result.getUserName());

    verify(timeSlotRepository).save(any(TimeSlot.class)); // 타임슬롯 수용 인원 업데이트
    verify(reservationRepository).save(any(Reservation.class));
  }

  @Test
  @DisplayName("본인이 아닌 사용자의 예약 생성 시 ForbiddenOperationException")
  void createReservation_ForbiddenUser() {
    // given
    requestDTO.setUserId(2L); // 다른 사용자 ID
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

    // when & then
    ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
        () -> reservationService.createReservation(requestDTO, "user@test.com"));

    assertEquals("본인의 예약만 생성할 수 있습니다", exception.getMessage());
  }

  @Test
  @DisplayName("예약 가능 시간 이전에 예약 생성 시 BadRequestException")
  void createReservation_BeforeOpenTime() {
    // given
    Booth futureBooth = Booth.builder().id(1L).name("Test Booth")
        .reservationOpenTime(LocalDateTime.now().plusHours(1)) // 미래 시간
        .build();
    futureBooth.setFestival(festival);
    futureBooth.setCreatedBy(hostUser);

    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(boothRepository.findById(1L)).thenReturn(Optional.of(futureBooth));
    when(festivalRepository.findById(1L)).thenReturn(Optional.of(festival));
    when(timeSlotRepository.findByIdWithLock(1L)).thenReturn(Optional.of(timeSlot));

    // when & then
    BadRequestException exception = assertThrows(BadRequestException.class,
        () -> reservationService.createReservation(requestDTO, "user@test.com"));

    assertTrue(exception.getMessage().contains("예약 가능 시간이 아닙니다"));
  }

  @Test
  @DisplayName("타임슬롯이 부스에 속하지 않으면 BadRequestException")
  void createReservation_TimeSlotNotBelongToBooth() {
    // given
    Booth otherBooth = Booth.builder().id(2L).build();
    timeSlot.setBooth(otherBooth);

    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
    when(festivalRepository.findById(1L)).thenReturn(Optional.of(festival));
    when(timeSlotRepository.findByIdWithLock(1L)).thenReturn(Optional.of(timeSlot));

    // when & then
    BadRequestException exception = assertThrows(BadRequestException.class,
        () -> reservationService.createReservation(requestDTO, "user@test.com"));

    assertEquals("타임슬롯이 해당 부스에 속하지 않습니다", exception.getMessage());
  }

  @Test
  @DisplayName("예약 불가능한 타임슬롯에 예약 생성 시 BadRequestException")
  void createReservation_UnavailableTimeSlot() {
    // given
    timeSlot.setStatus(TimeSlotStatus.CLOSED);

    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
    when(festivalRepository.findById(1L)).thenReturn(Optional.of(festival));
    when(timeSlotRepository.findByIdWithLock(1L)).thenReturn(Optional.of(timeSlot));

    // when & then
    BadRequestException exception = assertThrows(BadRequestException.class,
        () -> reservationService.createReservation(requestDTO, "user@test.com"));

    assertEquals("예약이 불가능한 타임슬롯입니다", exception.getMessage());
  }

  @Test
  @DisplayName("타임슬롯 수용 인원 초과 시 ConflictException")
  void createReservation_ExceedsCapacity() {
    // given
    requestDTO.setNumberOfPeople(8); // 현재 3명 + 8명 = 11명 > 최대 10명

    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
    when(festivalRepository.findById(1L)).thenReturn(Optional.of(festival));
    when(timeSlotRepository.findByIdWithLock(1L)).thenReturn(Optional.of(timeSlot));

    // when & then
    ConflictException exception = assertThrows(ConflictException.class,
        () -> reservationService.createReservation(requestDTO, "user@test.com"));

    assertTrue(exception.getMessage().contains("남은 자리가 부족합니다"));
  }

  @Test
  @DisplayName("이미 예약이 존재하는 타임슬롯에 중복 예약 시 BadRequestException")
  void createReservation_DuplicateReservation() {
    // given
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
    when(festivalRepository.findById(1L)).thenReturn(Optional.of(festival));
    when(timeSlotRepository.findByIdWithLock(1L)).thenReturn(Optional.of(timeSlot));
    when(reservationRepository.existsByUserIdAndTimeSlotId(1L, 1L)).thenReturn(true);

    // when & then
    BadRequestException exception = assertThrows(BadRequestException.class,
        () -> reservationService.createReservation(requestDTO, "user@test.com"));

    assertEquals("이미 해당 타임슬롯에 예약이 존재합니다", exception.getMessage());
  }

  @Test
  @DisplayName("잘못된 결제 방법으로 예약 생성 시 IllegalArgumentException")
  void createReservation_InvalidPaymentMethod() {
    // given
    requestDTO.setPaymentMethod("INVALID_METHOD");

    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
    when(festivalRepository.findById(1L)).thenReturn(Optional.of(festival));
    when(timeSlotRepository.findByIdWithLock(1L)).thenReturn(Optional.of(timeSlot));
    when(reservationRepository.existsByUserIdAndTimeSlotId(1L, 1L)).thenReturn(false);

    // when & then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> reservationService.createReservation(requestDTO, "user@test.com"));

    assertTrue(exception.getMessage().contains("Invalid payment method"));
  }

  @Test
  @DisplayName("예약을 성공적으로 취소한다")
  void cancelReservation_Success() {
    // given
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
    when(timeSlotRepository.findByIdWithLock(timeSlot.getId())).thenReturn(Optional.of(timeSlot));

    // when
    reservationService.cancelReservation(1L, "user@test.com");

    // then
    verify(reservationRepository).delete(reservation);
    verify(timeSlotRepository).save(any(TimeSlot.class)); // 타임슬롯 수용 인원 감소
  }

  @Test
  @DisplayName("본인의 예약이 아닌 경우 취소 시 ForbiddenOperationException")
  void cancelReservation_NotOwnReservation() {
    // given
    User otherUser = User.builder().id(3L).build();
    reservation.setUser(otherUser);

    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

    // when & then
    ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
        () -> reservationService.cancelReservation(1L, "user@test.com"));

    assertEquals("본인의 예약만 취소할 수 있습니다", exception.getMessage());
    verify(reservationRepository, never()).delete(any());
  }

  @Test
  @DisplayName("사용자의 예약 목록을 조회한다")
  void getUserReservations() {
    // given
    List<Reservation> reservations = Arrays.asList(reservation);
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(reservationRepository.findByUserIdWithTimeSlot(1L)).thenReturn(reservations);

    // when
    List<ReservationResponseDTO> result = reservationService.getUserReservations("user@test.com");

    // then
    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).getReservationId());
    assertEquals("Test User", result.get(0).getUserName());
    verify(reservationRepository).findByUserIdWithTimeSlot(1L);
  }

  @Test
  @DisplayName("HOST가 자신의 부스 타임슬롯 예약 현황을 조회한다")
  void getTimeSlotReservations_Host() {
    // given
    List<Reservation> reservations = Arrays.asList(reservation);
    when(userRepository.findByEmail("host@test.com")).thenReturn(Optional.of(hostUser));
    when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
    when(reservationRepository.findByTimeSlotId(1L)).thenReturn(reservations);

    // when
    List<ReservationResponseDTO> result = reservationService.getTimeSlotReservations(1L,
        "host@test.com");

    // then
    assertEquals(1, result.size());
    verify(reservationRepository).findByTimeSlotId(1L);
  }

  @Test
  @DisplayName("HOST가 다른 사용자의 부스 타임슬롯 조회 시 ForbiddenOperationException")
  void getTimeSlotReservations_Host_OtherBooth() {
    // given
    User otherHost = User.builder().id(3L).role(Role.HOST).build();
    booth.setCreatedBy(otherHost);

    when(userRepository.findByEmail("host@test.com")).thenReturn(Optional.of(hostUser));
    when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));

    // when & then
    ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
        () -> reservationService.getTimeSlotReservations(1L, "host@test.com"));

    assertEquals("자신의 부스 예약만 조회할 수 있습니다", exception.getMessage());
  }

  @Test
  @DisplayName("USER가 타임슬롯 예약 현황 조회 시 ForbiddenOperationException")
  void getTimeSlotReservations_User_Forbidden() {
    // given
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));

    // when & then
    ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
        () -> reservationService.getTimeSlotReservations(1L, "user@test.com"));

    assertEquals("일반 사용자는 예약 현황을 조회할 수 없습니다", exception.getMessage());
  }

  @Test
  @DisplayName("존재하지 않는 사용자로 예약 생성 시 ResourceNotFoundException")
  void createReservation_UserNotFound() {
    // given
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());

    // when & then
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> reservationService.createReservation(requestDTO, "user@test.com"));

    assertEquals("User not found: user@test.com", exception.getMessage());
  }

  @Test
  @DisplayName("존재하지 않는 부스로 예약 생성 시 ResourceNotFoundException")
  void createReservation_BoothNotFound() {
    // given
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(boothRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> reservationService.createReservation(requestDTO, "user@test.com"));

    assertEquals("Booth not found: 1", exception.getMessage());
  }

  @Test
  @DisplayName("존재하지 않는 축제로 예약 생성 시 ResourceNotFoundException")
  void createReservation_FestivalNotFound() {
    // given
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
    when(festivalRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> reservationService.createReservation(requestDTO, "user@test.com"));

    assertEquals("Festival not found: 1", exception.getMessage());
  }

  @Test
  @DisplayName("존재하지 않는 타임슬롯으로 예약 생성 시 ResourceNotFoundException")
  void createReservation_TimeSlotNotFound() {
    // given
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
    when(festivalRepository.findById(1L)).thenReturn(Optional.of(festival));
    when(timeSlotRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

    // when & then
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> reservationService.createReservation(requestDTO, "user@test.com"));

    assertEquals("TimeSlot not found: 1", exception.getMessage());
  }

  @Test
  @DisplayName("비관적 락을 사용한 타임슬롯 조회가 정상 작동한다")
  void createReservation_PessimisticLockWorks() {
    // given
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
    when(festivalRepository.findById(1L)).thenReturn(Optional.of(festival));
    when(timeSlotRepository.findByIdWithLock(1L)).thenReturn(Optional.of(timeSlot));
    when(reservationRepository.existsByUserIdAndTimeSlotId(1L, 1L)).thenReturn(false);
    when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

    // when
    ReservationResponseDTO result = reservationService.createReservation(requestDTO,
        "user@test.com");

    // then
    assertNotNull(result);
    verify(timeSlotRepository).findByIdWithLock(1L); // 비관적 락 사용 확인
    verify(timeSlotRepository, never()).findById(1L); // 일반 조회 메서드는 사용하지 않음
  }

  @Test
  @DisplayName("여러 명이 예약할 때 정확한 수용 인원 증가")
  void createReservation_MultiplePersonReservation() {
    // given
    requestDTO.setNumberOfPeople(3); // 3명 예약

    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
    when(festivalRepository.findById(1L)).thenReturn(Optional.of(festival));
    when(timeSlotRepository.findByIdWithLock(1L)).thenReturn(Optional.of(timeSlot));
    when(reservationRepository.existsByUserIdAndTimeSlotId(1L, 1L)).thenReturn(false);
    when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

    int initialCapacity = timeSlot.getCurrentCapacity();

    // when
    reservationService.createReservation(requestDTO, "user@test.com");

    // then
    assertEquals(initialCapacity + 3, timeSlot.getCurrentCapacity()); // 3명 증가 확인
    verify(timeSlotRepository).save(timeSlot);
  }

  @Test
  @DisplayName("예약 취소 시 비관적 락을 사용한다")
  void cancelReservation_PessimisticLockWorks() {
    // given
    reservation.setNumberOfPeople(2); // 2명 예약
    timeSlot.setCurrentCapacity(5);

    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
    when(timeSlotRepository.findByIdWithLock(1L)).thenReturn(Optional.of(timeSlot));

    // when
    reservationService.cancelReservation(1L, "user@test.com");

    // then
    verify(timeSlotRepository).findByIdWithLock(1L); // 비관적 락 사용 확인
    assertEquals(3, timeSlot.getCurrentCapacity()); // 2명 감소 확인 (5 - 2 = 3)
    verify(reservationRepository).delete(reservation);
  }
} 