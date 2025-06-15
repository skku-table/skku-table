package com.skkutable.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.domain.Role;
import com.skkutable.domain.TimeSlot;
import com.skkutable.domain.TimeSlotStatus;
import com.skkutable.domain.User;
import com.skkutable.repository.jpa.TimeSlotJpaRepository;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TimeSlotRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private TimeSlotJpaRepository timeSlotRepository;

  @Autowired
  private BoothRepository boothRepository;

  @Autowired
  private FestivalRepository festivalRepository;

  private User hostUser;
  private Festival festival;
  private Booth booth1;
  private Booth booth2;
  private TimeSlot timeSlot1;
  private TimeSlot timeSlot2;
  private TimeSlot timeSlot3;
  private TimeSlot timeSlot4;

  @BeforeEach
  void setUp() {
    // 사용자 생성
    hostUser = User.builder().name("Host User").email("host@test.com").password("password")
        .role(Role.HOST).build();
    hostUser = entityManager.persistAndFlush(hostUser);

    // 축제 생성
    festival = Festival.builder().name("Test Festival").description("Test Festival Description")
        .location("Seoul").startDate(new Date()).endDate(new Date()).likeCount(0).build();
    festival = entityManager.persistAndFlush(festival);

    // 부스 생성
    booth1 = Booth.builder().name("Food Booth").host("Food Host").location("A-1")
        .description("Delicious food").startDateTime(LocalDateTime.of(2024, 1, 1, 9, 0))
        .endDateTime(LocalDateTime.of(2024, 1, 1, 18, 0)).likeCount(0).build();
    booth1.setFestival(festival);
    booth1.setCreatedBy(hostUser);
    booth1 = entityManager.persistAndFlush(booth1);

    booth2 = Booth.builder().name("Game Booth").host("Game Host").location("B-1")
        .description("Fun games").startDateTime(LocalDateTime.of(2024, 1, 1, 10, 0))
        .endDateTime(LocalDateTime.of(2024, 1, 1, 17, 0)).likeCount(0).build();
    booth2.setFestival(festival);
    booth2.setCreatedBy(hostUser);
    booth2 = entityManager.persistAndFlush(booth2);

    // 타임슬롯 생성
    timeSlot1 = TimeSlot.builder().booth(booth1).startTime(LocalDateTime.of(2024, 1, 1, 10, 0))
        .endTime(LocalDateTime.of(2024, 1, 1, 11, 0)).maxCapacity(10).currentCapacity(3)
        .status(TimeSlotStatus.AVAILABLE).build();
    timeSlot1 = entityManager.persistAndFlush(timeSlot1);

    timeSlot2 = TimeSlot.builder().booth(booth1).startTime(LocalDateTime.of(2024, 1, 1, 11, 0))
        .endTime(LocalDateTime.of(2024, 1, 1, 12, 0)).maxCapacity(5).currentCapacity(5)
        .status(TimeSlotStatus.FULL).build();
    timeSlot2 = entityManager.persistAndFlush(timeSlot2);

    timeSlot3 = TimeSlot.builder().booth(booth1).startTime(LocalDateTime.of(2024, 1, 1, 12, 0))
        .endTime(LocalDateTime.of(2024, 1, 1, 13, 0)).maxCapacity(8).currentCapacity(0)
        .status(TimeSlotStatus.CLOSED).build();
    timeSlot3 = entityManager.persistAndFlush(timeSlot3);

    timeSlot4 = TimeSlot.builder().booth(booth2).startTime(LocalDateTime.of(2024, 1, 1, 14, 0))
        .endTime(LocalDateTime.of(2024, 1, 1, 15, 0)).maxCapacity(6).currentCapacity(2)
        .status(TimeSlotStatus.AVAILABLE).build();
    timeSlot4 = entityManager.persistAndFlush(timeSlot4);

    entityManager.clear();
  }

  @Test
  @DisplayName("부스 ID로 타임슬롯을 조회한다")
  void findByBoothId() {
    // when
    List<TimeSlot> booth1TimeSlots = timeSlotRepository.findByBoothId(booth1.getId());
    List<TimeSlot> booth2TimeSlots = timeSlotRepository.findByBoothId(booth2.getId());

    // then
    assertEquals(3, booth1TimeSlots.size());
    assertEquals(1, booth2TimeSlots.size());

    // 부스1의 타임슬롯들 확인
    assertTrue(booth1TimeSlots.stream().anyMatch(ts -> ts.getStartTime().getHour() == 10));
    assertTrue(booth1TimeSlots.stream().anyMatch(ts -> ts.getStartTime().getHour() == 11));
    assertTrue(booth1TimeSlots.stream().anyMatch(ts -> ts.getStartTime().getHour() == 12));

    // 부스2의 타임슬롯 확인
    assertEquals(14, booth2TimeSlots.get(0).getStartTime().getHour());
  }

  @Test
  @DisplayName("부스 ID와 상태로 타임슬롯을 조회한다")
  void findByBoothIdAndStatus() {
    // when
    List<TimeSlot> availableSlots = timeSlotRepository.findByBoothIdAndStatus(booth1.getId(),
        TimeSlotStatus.AVAILABLE);
    List<TimeSlot> fullSlots = timeSlotRepository.findByBoothIdAndStatus(booth1.getId(),
        TimeSlotStatus.FULL);
    List<TimeSlot> closedSlots = timeSlotRepository.findByBoothIdAndStatus(booth1.getId(),
        TimeSlotStatus.CLOSED);

    // then
    assertEquals(1, availableSlots.size());
    assertEquals(10, availableSlots.get(0).getStartTime().getHour());

    assertEquals(1, fullSlots.size());
    assertEquals(11, fullSlots.get(0).getStartTime().getHour());

    assertEquals(1, closedSlots.size());
    assertEquals(12, closedSlots.get(0).getStartTime().getHour());
  }

  @Test
  @DisplayName("타임슬롯 ID와 부스 ID로 타임슬롯을 조회한다")
  void findByIdAndBoothId() {
    // when
    Optional<TimeSlot> found = timeSlotRepository.findByIdAndBoothId(timeSlot1.getId(),
        booth1.getId());
    Optional<TimeSlot> notFound = timeSlotRepository.findByIdAndBoothId(timeSlot1.getId(),
        booth2.getId()); // 다른 부스 ID

    // then
    assertTrue(found.isPresent());
    assertEquals(timeSlot1.getId(), found.get().getId());
    assertEquals(booth1.getId(), found.get().getBooth().getId());

    assertFalse(notFound.isPresent());
  }

  @Test
  @DisplayName("부스 ID와 시간 범위로 타임슬롯을 조회한다")
  void findByBoothIdAndTimeBetween() {
    // when
    List<TimeSlot> timeSlots = timeSlotRepository.findByBoothIdAndTimeBetween(booth1.getId(),
        LocalDateTime.of(2024, 1, 1, 10, 30), LocalDateTime.of(2024, 1, 1, 12, 30));

    // then
    assertEquals(3, timeSlots.size());

    // 10:00-11:00, 11:00-12:00, 12:00-13:00 슬롯이 포함되어야 함
    assertTrue(timeSlots.stream()
        .anyMatch(ts -> ts.getStartTime().equals(LocalDateTime.of(2024, 1, 1, 10, 0))));
    assertTrue(timeSlots.stream()
        .anyMatch(ts -> ts.getStartTime().equals(LocalDateTime.of(2024, 1, 1, 11, 0))));
    assertTrue(timeSlots.stream()
        .anyMatch(ts -> ts.getStartTime().equals(LocalDateTime.of(2024, 1, 1, 12, 0))));
  }

  @Test
  @DisplayName("부스의 예약 가능한 타임슬롯을 조회한다")
  void findAvailableTimeSlotsByBoothId() {
    // when
    List<TimeSlot> availableSlots = timeSlotRepository.findAvailableTimeSlotsByBoothId(
        booth1.getId());

    // then
    assertEquals(1, availableSlots.size());

    TimeSlot availableSlot = availableSlots.get(0);
    assertEquals(TimeSlotStatus.AVAILABLE, availableSlot.getStatus());
    assertTrue(availableSlot.getCurrentCapacity() < availableSlot.getMaxCapacity());
    assertEquals(10, availableSlot.getStartTime().getHour());

    // booth2의 예약 가능한 슬롯도 확인
    List<TimeSlot> booth2AvailableSlots = timeSlotRepository.findAvailableTimeSlotsByBoothId(
        booth2.getId());
    assertEquals(1, booth2AvailableSlots.size());
  }

  @Test
  @DisplayName("부스 ID와 시작/종료 시간으로 중복 타임슬롯 존재 여부를 확인한다")
  void existsByBoothIdAndStartTimeAndEndTime() {
    // when & then
    // 기존에 존재하는 타임슬롯
    assertTrue(timeSlotRepository.existsByBoothIdAndStartTimeAndEndTime(booth1.getId(),
        LocalDateTime.of(2024, 1, 1, 10, 0), LocalDateTime.of(2024, 1, 1, 11, 0)));

    // 존재하지 않는 타임슬롯
    assertFalse(timeSlotRepository.existsByBoothIdAndStartTimeAndEndTime(booth1.getId(),
        LocalDateTime.of(2024, 1, 1, 15, 0), LocalDateTime.of(2024, 1, 1, 16, 0)));

    // 다른 부스의 타임슬롯
    assertFalse(timeSlotRepository.existsByBoothIdAndStartTimeAndEndTime(booth2.getId(),
        LocalDateTime.of(2024, 1, 1, 10, 0), LocalDateTime.of(2024, 1, 1, 11, 0)));
  }

  @Test
  @DisplayName("존재하지 않는 부스 ID로 조회 시 빈 리스트를 반환한다")
  void findByBoothId_NonExistentBooth() {
    // when
    List<TimeSlot> timeSlots = timeSlotRepository.findByBoothId(999L);

    // then
    assertTrue(timeSlots.isEmpty());
  }

  @Test
  @DisplayName("타임슬롯 생성 및 업데이트 시간이 자동으로 설정된다")
  void testTimestamps() {
    // given
    TimeSlot newTimeSlot = TimeSlot.builder().booth(booth1)
        .startTime(LocalDateTime.of(2024, 1, 1, 16, 0)).endTime(LocalDateTime.of(2024, 1, 1, 17, 0))
        .maxCapacity(5).build();

    // when
    // Use JpaRepository's save method explicitly
    TimeSlot saved = ((org.springframework.data.repository.CrudRepository<TimeSlot, Long>) timeSlotRepository).save(
        newTimeSlot);
    entityManager.flush();

    // then
    assertNotNull(saved.getCreatedAt());
    assertNotNull(saved.getUpdatedAt());

    // 업데이트 테스트
    LocalDateTime originalUpdatedAt = saved.getUpdatedAt();
    saved.setMaxCapacity(8);

    // Use JpaRepository's save method explicitly
    TimeSlot updated = ((org.springframework.data.repository.CrudRepository<TimeSlot, Long>) timeSlotRepository).save(
        saved);
    entityManager.flush();

    assertNotEquals(originalUpdatedAt, updated.getUpdatedAt());
  }

  @Test
  @DisplayName("타임슬롯과 부스의 연관관계가 올바르게 설정된다")
  void testTimeSlotBoothRelationship() {
    // when
    TimeSlot foundTimeSlot = ((org.springframework.data.repository.CrudRepository<TimeSlot, Long>) timeSlotRepository).findById(
        timeSlot1.getId()).orElseThrow();

    // then
    assertNotNull(foundTimeSlot.getBooth());
    assertEquals(booth1.getId(), foundTimeSlot.getBooth().getId());
    assertEquals("Food Booth", foundTimeSlot.getBooth().getName());
  }
} 