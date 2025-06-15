package com.skkutable.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TimeSlotTest {

  private TimeSlot timeSlot;
  private Booth booth;

  @BeforeEach
  void setUp() {
    booth = Booth.builder().id(1L).name("Test Booth").build();

    timeSlot = TimeSlot.builder().id(1L).booth(booth).startTime(LocalDateTime.of(2024, 1, 1, 10, 0))
        .endTime(LocalDateTime.of(2024, 1, 1, 11, 0)).maxCapacity(10).currentCapacity(0)
        .status(TimeSlotStatus.AVAILABLE).build();
  }

  @Test
  @DisplayName("타임슬롯 생성 시 기본값이 올바르게 설정된다")
  void createTimeSlot_DefaultValues() {
    // given
    TimeSlot newTimeSlot = TimeSlot.builder().booth(booth).startTime(LocalDateTime.now())
        .endTime(LocalDateTime.now().plusHours(1)).maxCapacity(5).build();

    // then
    assertEquals(0, newTimeSlot.getCurrentCapacity());
    assertEquals(TimeSlotStatus.AVAILABLE, newTimeSlot.getStatus());
  }

  @Test
  @DisplayName("수용 인원 증가 시 현재 인원이 증가한다")
  void incrementCapacity_Success() {
    // given
    assertEquals(0, timeSlot.getCurrentCapacity());
    assertEquals(TimeSlotStatus.AVAILABLE, timeSlot.getStatus());

    // when
    timeSlot.incrementCapacity();

    // then
    assertEquals(1, timeSlot.getCurrentCapacity());
    assertEquals(TimeSlotStatus.AVAILABLE, timeSlot.getStatus());
  }

  @Test
  @DisplayName("수용 인원이 최대치에 도달하면 상태가 FULL로 변경된다")
  void incrementCapacity_BecomeFull() {
    // given
    timeSlot.setCurrentCapacity(9); // 최대 10명 중 9명

    // when
    timeSlot.incrementCapacity();

    // then
    assertEquals(10, timeSlot.getCurrentCapacity());
    assertEquals(TimeSlotStatus.FULL, timeSlot.getStatus());
  }

  @Test
  @DisplayName("이미 최대 인원인 경우 수용 인원 증가가 되지 않는다")
  void incrementCapacity_AlreadyFull() {
    // given
    timeSlot.setCurrentCapacity(10);
    timeSlot.setStatus(TimeSlotStatus.FULL);

    // when
    timeSlot.incrementCapacity();

    // then
    assertEquals(10, timeSlot.getCurrentCapacity());
    assertEquals(TimeSlotStatus.FULL, timeSlot.getStatus());
  }

  @Test
  @DisplayName("수용 인원 감소 시 현재 인원이 감소한다")
  void decrementCapacity_Success() {
    // given
    timeSlot.setCurrentCapacity(5);

    // when
    timeSlot.decrementCapacity();

    // then
    assertEquals(4, timeSlot.getCurrentCapacity());
  }

  @Test
  @DisplayName("FULL 상태에서 수용 인원 감소 시 상태가 AVAILABLE로 변경된다")
  void decrementCapacity_BecomeAvailable() {
    // given
    timeSlot.setCurrentCapacity(10);
    timeSlot.setStatus(TimeSlotStatus.FULL);

    // when
    timeSlot.decrementCapacity();

    // then
    assertEquals(9, timeSlot.getCurrentCapacity());
    assertEquals(TimeSlotStatus.AVAILABLE, timeSlot.getStatus());
  }

  @Test
  @DisplayName("현재 인원이 0인 경우 수용 인원 감소가 되지 않는다")
  void decrementCapacity_AlreadyZero() {
    // given
    assertEquals(0, timeSlot.getCurrentCapacity());

    // when
    timeSlot.decrementCapacity();

    // then
    assertEquals(0, timeSlot.getCurrentCapacity());
  }

  @Test
  @DisplayName("예약 가능 여부를 올바르게 판단한다 - 가능한 경우")
  void isAvailable_True() {
    // given
    timeSlot.setCurrentCapacity(5);
    timeSlot.setStatus(TimeSlotStatus.AVAILABLE);

    // when & then
    assertTrue(timeSlot.isAvailable());
  }

  @Test
  @DisplayName("예약 가능 여부를 올바르게 판단한다 - 불가능한 경우 (FULL 상태)")
  void isAvailable_False_FullStatus() {
    // given
    timeSlot.setCurrentCapacity(10);
    timeSlot.setStatus(TimeSlotStatus.FULL);

    // when & then
    assertFalse(timeSlot.isAvailable());
  }

  @Test
  @DisplayName("예약 가능 여부를 올바르게 판단한다 - 불가능한 경우 (CLOSED 상태)")
  void isAvailable_False_ClosedStatus() {
    // given
    timeSlot.setCurrentCapacity(5);
    timeSlot.setStatus(TimeSlotStatus.CLOSED);

    // when & then
    assertFalse(timeSlot.isAvailable());
  }

  @Test
  @DisplayName("예약 가능 여부를 올바르게 판단한다 - 불가능한 경우 (최대 인원 도달)")
  void isAvailable_False_MaxCapacity() {
    // given
    timeSlot.setCurrentCapacity(10);
    timeSlot.setStatus(TimeSlotStatus.AVAILABLE);

    // when & then
    assertFalse(timeSlot.isAvailable());
  }

  @Test
  @DisplayName("특정 인원을 수용할 수 있는지 올바르게 판단한다 - 가능한 경우")
  void canAccommodate_True() {
    // given
    timeSlot.setCurrentCapacity(3);

    // when & then
    assertTrue(timeSlot.canAccommodate(5)); // 3 + 5 = 8 <= 10
    assertTrue(timeSlot.canAccommodate(7)); // 3 + 7 = 10 <= 10
  }

  @Test
  @DisplayName("특정 인원을 수용할 수 있는지 올바르게 판단한다 - 불가능한 경우")
  void canAccommodate_False() {
    // given
    timeSlot.setCurrentCapacity(3);

    // when & then
    assertFalse(timeSlot.canAccommodate(8)); // 3 + 8 = 11 > 10
  }

  @Test
  @DisplayName("현재 인원이 최대 인원인 경우 추가 인원을 수용할 수 없다")
  void canAccommodate_False_AlreadyFull() {
    // given
    timeSlot.setCurrentCapacity(10);

    // when & then
    assertFalse(timeSlot.canAccommodate(1));
  }

  @Test
  @DisplayName("0명을 수용할 수 있는지 확인")
  void canAccommodate_ZeroPeople() {
    // given
    timeSlot.setCurrentCapacity(10);

    // when & then
    assertTrue(timeSlot.canAccommodate(0)); // 현재 인원 + 0 = 10 <= 10
  }
} 