package com.skkutable.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Role;
import com.skkutable.domain.TimeSlot;
import com.skkutable.domain.TimeSlotStatus;
import com.skkutable.domain.User;
import com.skkutable.dto.TimeSlotCreateDto;
import com.skkutable.dto.TimeSlotResponseDto;
import com.skkutable.dto.TimeSlotUpdateDto;
import com.skkutable.exception.BadRequestException;
import com.skkutable.exception.ConflictException;
import com.skkutable.exception.ForbiddenOperationException;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.TimeSlotRepository;
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
class TimeSlotServiceTest {

  @Mock
  private TimeSlotRepository timeSlotRepository;

  @Mock
  private BoothService boothService;

  @Mock
  private UserService userService;

  @InjectMocks
  private TimeSlotService timeSlotService;

  private User hostUser;
  private User adminUser;
  private User otherHostUser;
  private Booth booth;
  private TimeSlot timeSlot;
  private TimeSlotCreateDto createDto;
  private TimeSlotUpdateDto updateDto;

  @BeforeEach
  void setUp() {
    hostUser = User.builder().id(1L).name("Host User").email("host@test.com").role(Role.HOST)
        .build();

    adminUser = User.builder().id(2L).name("Admin User").email("admin@test.com").role(Role.ADMIN)
        .build();

    otherHostUser = User.builder().id(3L).name("Other Host").email("other@test.com").role(Role.HOST)
        .build();

    booth = Booth.builder().id(1L).name("Test Booth")
        .startDateTime(LocalDateTime.of(2024, 1, 1, 9, 0))
        .endDateTime(LocalDateTime.of(2024, 1, 1, 18, 0)).build();
    booth.setCreatedBy(hostUser);

    timeSlot = TimeSlot.builder().id(1L).booth(booth).startTime(LocalDateTime.of(2024, 1, 1, 10, 0))
        .endTime(LocalDateTime.of(2024, 1, 1, 11, 0)).maxCapacity(10).currentCapacity(0)
        .status(TimeSlotStatus.AVAILABLE).build();

    createDto = new TimeSlotCreateDto();
    createDto.setStartTime(LocalDateTime.of(2024, 1, 1, 14, 0));
    createDto.setEndTime(LocalDateTime.of(2024, 1, 1, 15, 0));
    createDto.setMaxCapacity(8);

    updateDto = new TimeSlotUpdateDto();
    updateDto.setMaxCapacity(12);
    updateDto.setStatus(TimeSlotStatus.CLOSED);
  }

  @Test
  @DisplayName("HOST가 자신의 부스에 타임슬롯을 생성한다")
  void createTimeSlot_Success_Host() {
    // given
    when(userService.getCurrentUser("host@test.com")).thenReturn(hostUser);
    when(boothService.findBoothById(1L)).thenReturn(booth);
    when(timeSlotRepository.existsByBoothIdAndStartTimeAndEndTime(anyLong(),
        any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);
    when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(timeSlot);

    // when
    TimeSlotResponseDto result = timeSlotService.createTimeSlot(1L, createDto, "host@test.com");

    // then
    assertNotNull(result);
    verify(userService).getCurrentUser("host@test.com");
    verify(boothService).findBoothById(1L);
    verify(timeSlotRepository).existsByBoothIdAndStartTimeAndEndTime(1L, createDto.getStartTime(),
        createDto.getEndTime());
    verify(timeSlotRepository).save(any(TimeSlot.class));
  }

  @Test
  @DisplayName("ADMIN이 모든 부스에 타임슬롯을 생성한다")
  void createTimeSlot_Success_Admin() {
    // given
    when(userService.getCurrentUser("admin@test.com")).thenReturn(adminUser);
    when(boothService.findBoothById(1L)).thenReturn(booth);
    when(timeSlotRepository.existsByBoothIdAndStartTimeAndEndTime(anyLong(),
        any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);
    when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(timeSlot);

    // when
    TimeSlotResponseDto result = timeSlotService.createTimeSlot(1L, createDto, "admin@test.com");

    // then
    assertNotNull(result);
    verify(timeSlotRepository).save(any(TimeSlot.class));
  }

  @Test
  @DisplayName("HOST가 다른 사용자의 부스에 타임슬롯 생성 시 ForbiddenOperationException")
  void createTimeSlot_ForbiddenOperation() {
    // given
    when(userService.getCurrentUser("other@test.com")).thenReturn(otherHostUser);
    when(boothService.findBoothById(1L)).thenReturn(booth);

    // when & then
    ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
        () -> timeSlotService.createTimeSlot(1L, createDto, "other@test.com"));

    assertEquals("자신이 생성한 부스의 타임슬롯만 생성할 수 있습니다", exception.getMessage());
    verify(timeSlotRepository, never()).save(any());
  }

  @Test
  @DisplayName("시작 시간이 종료 시간보다 늦으면 BadRequestException")
  void createTimeSlot_InvalidTime() {
    // given
    createDto.setStartTime(LocalDateTime.of(2024, 1, 1, 15, 0));
    createDto.setEndTime(LocalDateTime.of(2024, 1, 1, 14, 0)); // 시작 > 종료

    when(userService.getCurrentUser("host@test.com")).thenReturn(hostUser);
    when(boothService.findBoothById(1L)).thenReturn(booth);

    // when & then
    BadRequestException exception = assertThrows(BadRequestException.class,
        () -> timeSlotService.createTimeSlot(1L, createDto, "host@test.com"));

    assertEquals("시작 시간은 종료 시간보다 앞서야 합니다", exception.getMessage());
  }

  @Test
  @DisplayName("부스 운영 시간을 벗어나면 BadRequestException")
  void createTimeSlot_OutOfBoothTime() {
    // given
    createDto.setStartTime(LocalDateTime.of(2024, 1, 1, 8, 0)); // 부스 시작 전
    createDto.setEndTime(LocalDateTime.of(2024, 1, 1, 9, 0));

    when(userService.getCurrentUser("host@test.com")).thenReturn(hostUser);
    when(boothService.findBoothById(1L)).thenReturn(booth);

    // when & then
    BadRequestException exception = assertThrows(BadRequestException.class,
        () -> timeSlotService.createTimeSlot(1L, createDto, "host@test.com"));

    assertEquals("타임슬롯은 부스 운영 시간 내에 있어야 합니다", exception.getMessage());
  }

  @Test
  @DisplayName("중복 시간대 타임슬롯 생성 시 ConflictException")
  void createTimeSlot_DuplicateTime() {
    // given
    when(userService.getCurrentUser("host@test.com")).thenReturn(hostUser);
    when(boothService.findBoothById(1L)).thenReturn(booth);
    when(timeSlotRepository.existsByBoothIdAndStartTimeAndEndTime(anyLong(),
        any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);

    // when & then
    ConflictException exception = assertThrows(ConflictException.class,
        () -> timeSlotService.createTimeSlot(1L, createDto, "host@test.com"));

    assertEquals("이미 동일한 시간대의 타임슬롯이 존재합니다", exception.getMessage());
  }

  @Test
  @DisplayName("타임슬롯을 성공적으로 수정한다")
  void updateTimeSlot_Success() {
    // given
    when(userService.getCurrentUser("host@test.com")).thenReturn(hostUser);
    when(boothService.findBoothById(1L)).thenReturn(booth);
    when(timeSlotRepository.findByIdAndBoothId(1L, 1L)).thenReturn(Optional.of(timeSlot));

    // when
    TimeSlotResponseDto result = timeSlotService.updateTimeSlot(1L, 1L, updateDto, "host@test.com");

    // then
    assertNotNull(result);
    assertEquals(12, timeSlot.getMaxCapacity());
    assertEquals(TimeSlotStatus.CLOSED, timeSlot.getStatus());
  }

  @Test
  @DisplayName("예약이 있는 타임슬롯의 시간 변경 시 BadRequestException")
  void updateTimeSlot_HasReservations() {
    // given
    timeSlot.setCurrentCapacity(5); // 예약이 있음
    updateDto.setStartTime(LocalDateTime.of(2024, 1, 1, 15, 0));

    when(userService.getCurrentUser("host@test.com")).thenReturn(hostUser);
    when(boothService.findBoothById(1L)).thenReturn(booth);
    when(timeSlotRepository.findByIdAndBoothId(1L, 1L)).thenReturn(Optional.of(timeSlot));

    // when & then
    BadRequestException exception = assertThrows(BadRequestException.class,
        () -> timeSlotService.updateTimeSlot(1L, 1L, updateDto, "host@test.com"));

    assertEquals("예약이 존재하는 타임슬롯의 시간은 변경할 수 없습니다", exception.getMessage());
  }

  @Test
  @DisplayName("최대 수용 인원을 현재 예약 인원보다 작게 설정 시 BadRequestException")
  void updateTimeSlot_MaxCapacityTooSmall() {
    // given
    timeSlot.setCurrentCapacity(8);
    updateDto.setMaxCapacity(5); // 현재 예약 인원보다 작음

    when(userService.getCurrentUser("host@test.com")).thenReturn(hostUser);
    when(boothService.findBoothById(1L)).thenReturn(booth);
    when(timeSlotRepository.findByIdAndBoothId(1L, 1L)).thenReturn(Optional.of(timeSlot));

    // when & then
    BadRequestException exception = assertThrows(BadRequestException.class,
        () -> timeSlotService.updateTimeSlot(1L, 1L, updateDto, "host@test.com"));

    assertEquals("최대 수용 인원은 현재 예약 인원보다 작을 수 없습니다", exception.getMessage());
  }

  @Test
  @DisplayName("예약이 있는 타임슬롯 삭제 시 BadRequestException")
  void deleteTimeSlot_HasReservations() {
    // given
    timeSlot.setCurrentCapacity(3);

    when(userService.getCurrentUser("host@test.com")).thenReturn(hostUser);
    when(boothService.findBoothById(1L)).thenReturn(booth);
    when(timeSlotRepository.findByIdAndBoothId(1L, 1L)).thenReturn(Optional.of(timeSlot));

    // when & then
    BadRequestException exception = assertThrows(BadRequestException.class,
        () -> timeSlotService.deleteTimeSlot(1L, 1L, "host@test.com"));

    assertEquals("예약이 존재하는 타임슬롯은 삭제할 수 없습니다", exception.getMessage());
    verify(timeSlotRepository, never()).delete(any());
  }

  @Test
  @DisplayName("타임슬롯을 성공적으로 삭제한다")
  void deleteTimeSlot_Success() {
    // given
    when(userService.getCurrentUser("host@test.com")).thenReturn(hostUser);
    when(boothService.findBoothById(1L)).thenReturn(booth);
    when(timeSlotRepository.findByIdAndBoothId(1L, 1L)).thenReturn(Optional.of(timeSlot));

    // when
    timeSlotService.deleteTimeSlot(1L, 1L, "host@test.com");

    // then
    verify(timeSlotRepository).delete(timeSlot);
  }

  @Test
  @DisplayName("부스의 모든 타임슬롯을 조회한다")
  void getTimeSlotsByBooth() {
    // given
    List<TimeSlot> timeSlots = Arrays.asList(timeSlot);
    when(boothService.findBoothById(1L)).thenReturn(booth);
    when(timeSlotRepository.findByBoothId(1L)).thenReturn(timeSlots);

    // when
    List<TimeSlotResponseDto> result = timeSlotService.getTimeSlotsByBooth(1L);

    // then
    assertEquals(1, result.size());
    verify(boothService).findBoothById(1L);
    verify(timeSlotRepository).findByBoothId(1L);
  }

  @Test
  @DisplayName("부스의 예약 가능한 타임슬롯을 조회한다")
  void getAvailableTimeSlots() {
    // given
    List<TimeSlot> timeSlots = Arrays.asList(timeSlot);
    when(boothService.findBoothById(1L)).thenReturn(booth);
    when(timeSlotRepository.findAvailableTimeSlotsByBoothId(1L)).thenReturn(timeSlots);

    // when
    List<TimeSlotResponseDto> result = timeSlotService.getAvailableTimeSlots(1L);

    // then
    assertEquals(1, result.size());
    verify(boothService).findBoothById(1L);
    verify(timeSlotRepository).findAvailableTimeSlotsByBoothId(1L);
  }

  @Test
  @DisplayName("존재하지 않는 타임슬롯 조회 시 ResourceNotFoundException")
  void findTimeSlotById_NotFound() {
    // given
    when(timeSlotRepository.findById(999L)).thenReturn(Optional.empty());

    // when & then
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> timeSlotService.findTimeSlotById(999L));

    assertEquals("TimeSlot not found: 999", exception.getMessage());
  }

  @Test
  @DisplayName("ID와 부스 ID로 타임슬롯을 조회한다")
  void findTimeSlotByIdAndBoothId_Success() {
    // given
    when(timeSlotRepository.findByIdAndBoothId(1L, 1L)).thenReturn(Optional.of(timeSlot));

    // when
    TimeSlot result = timeSlotService.findTimeSlotByIdAndBoothId(1L, 1L);

    // then
    assertEquals(timeSlot, result);
  }

  @Test
  @DisplayName("존재하지 않는 타임슬롯을 부스 ID로 조회 시 ResourceNotFoundException")
  void findTimeSlotByIdAndBoothId_NotFound() {
    // given
    when(timeSlotRepository.findByIdAndBoothId(1L, 1L)).thenReturn(Optional.empty());

    // when & then
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> timeSlotService.findTimeSlotByIdAndBoothId(1L, 1L));

    assertEquals("TimeSlot not found with id 1 in booth 1", exception.getMessage());
  }
} 