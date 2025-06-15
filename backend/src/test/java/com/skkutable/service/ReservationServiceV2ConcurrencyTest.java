package com.skkutable.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.domain.Role;
import com.skkutable.domain.TimeSlot;
import com.skkutable.domain.TimeSlotStatus;
import com.skkutable.domain.User;
import com.skkutable.dto.ReservationRequestDTO;
import com.skkutable.exception.BadRequestException;
import com.skkutable.exception.ConflictException;
import com.skkutable.repository.BoothRepository;
import com.skkutable.repository.FestivalRepository;
import com.skkutable.repository.ReservationRepository;
import com.skkutable.repository.TimeSlotRepository;
import com.skkutable.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationServiceV2ConcurrencyTest {

  @MockBean  // Cloudinary 의존성 제거
  private CloudinaryService cloudinaryService;

  @Autowired
  private ReservationServiceV2 reservationService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BoothRepository boothRepository;

  @Autowired
  private FestivalRepository festivalRepository;

  @Autowired
  private TimeSlotRepository timeSlotRepository;

  @Autowired
  private ReservationRepository reservationRepository;

  private Festival festival;
  private Booth booth;
  private TimeSlot timeSlot;
  private List<User> testUsers;

  @BeforeEach
  void setUp() {
    // 테스트 데이터 준비
    festival = Festival.builder().name("Test Festival")
        .startDate(new Date()) // LocalDateTime -> Date로 변경
        .endDate(new Date()) // LocalDateTime -> Date로 변경
        .build();
    festival = festivalRepository.save(festival);

    User hostUser = User.builder().name("Host User").email("host@test.com").password("password")
        .role(Role.HOST).build();
    hostUser = userRepository.save(hostUser);

    booth = Booth.builder().name("Test Booth").description("Test Description")
        .startDateTime(LocalDateTime.now().minusHours(1))
        .endDateTime(LocalDateTime.now().plusHours(5))
        .reservationOpenTime(LocalDateTime.now().minusHours(1)).build();
    booth.setFestival(festival);
    booth.setCreatedBy(hostUser);
    booth = boothRepository.save(booth);

    // 수용 인원이 적은 타임슬롯 생성 (동시성 테스트용)
    timeSlot = TimeSlot.builder().booth(booth).startTime(LocalDateTime.now().plusHours(1))
        .endTime(LocalDateTime.now().plusHours(2)).maxCapacity(5) // 제한된 수용 인원
        .currentCapacity(0).status(TimeSlotStatus.AVAILABLE).build();
    timeSlot = timeSlotRepository.save(timeSlot);

    // 테스트용 사용자들 생성
    testUsers = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      User user = User.builder().name("Test User " + i).email("user" + i + "@test.com")
          .password("password").role(Role.USER).build();
      testUsers.add(userRepository.save(user));
    }
  }

  @Test
  @Order(1)
  @DisplayName("동시에 여러 예약 요청 시 수용 인원을 초과하지 않는다")
  void concurrentReservations_ShouldNotExceedCapacity() throws InterruptedException {
    // given
    int numberOfThreads = 10;
    int maxCapacity = timeSlot.getMaxCapacity(); // 5
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch completeLatch = new CountDownLatch(numberOfThreads);
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);

    ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

    // when: 동시에 여러 예약 요청 실행
    for (int i = 0; i < numberOfThreads; i++) {
      final int userIndex = i;
      executor.submit(() -> {
        try {
          startLatch.await(); // 모든 스레드가 동시에 시작하도록 대기

          ReservationRequestDTO requestDTO = createReservationRequest(testUsers.get(userIndex), 1);
          reservationService.createReservation(requestDTO, testUsers.get(userIndex).getEmail());
          successCount.incrementAndGet();

        } catch (ConflictException e) {
          if (e.getMessage().contains("남은 자리가 부족합니다")) {
            System.out.println("예약 실패 (수용 인원 부족): " + e.getMessage());
            failureCount.incrementAndGet();
          } else {
            fail("Unexpected exception: " + e.getMessage());
          }
        } catch (Exception e) {
          fail("Unexpected exception: " + e.getMessage());
        } finally {
          completeLatch.countDown();
        }
      });
    }

    // 모든 스레드 동시 시작
    startLatch.countDown();
    completeLatch.await();
    executor.shutdown();

    // then: 검증
    TimeSlot updatedTimeSlot = timeSlotRepository.findById(timeSlot.getId()).orElseThrow();

    // 성공한 예약 수는 최대 수용 인원을 초과하지 않아야 함
    assertTrue(successCount.get() <= maxCapacity,
        "성공한 예약 수(" + successCount.get() + ")가 최대 수용 인원(" + maxCapacity + ")을 초과했습니다");

    // 실제 타임슬롯의 현재 수용 인원과 성공한 예약 수가 일치해야 함
    assertEquals(successCount.get(), updatedTimeSlot.getCurrentCapacity(),
        "타임슬롯 현재 수용 인원과 성공한 예약 수가 일치하지 않습니다");

    System.out.println("successCount: " + successCount.get());
    System.out.println("failureCount: " + failureCount.get());
    // 전체 시도 수는 numberOfThreads와 일치해야 함
    assertEquals(numberOfThreads, successCount.get() + failureCount.get(), "전체 시도 수가 예상과 다릅니다");

    // 데이터베이스의 실제 예약 수 확인
    int actualReservationCount = reservationRepository.findByTimeSlotId(timeSlot.getId()).size();
    assertEquals(successCount.get(), actualReservationCount, "데이터베이스의 실제 예약 수가 성공 카운트와 다릅니다");

    System.out.println("성공한 예약: " + successCount.get());
    System.out.println("실패한 예약: " + failureCount.get());
    System.out.println("최종 타임슬롯 수용 인원: " + updatedTimeSlot.getCurrentCapacity());
  }

  @Test
  @Order(2)
  @DisplayName("동시에 여러 명이 예약하는 경우 (인원수 다양)")
  void concurrentReservations_VariousNumberOfPeople() throws InterruptedException {
    // given
    int numberOfThreads = 8;
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch completeLatch = new CountDownLatch(numberOfThreads);
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger totalPeopleReserved = new AtomicInteger(0);

    ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

    // when: 다양한 인원수로 동시 예약 시도
    for (int i = 0; i < numberOfThreads; i++) {
      final int userIndex = i;
      final int numberOfPeople = (i % 3) + 1; // 1, 2, 3명 순환

      executor.submit(() -> {
        try {
          startLatch.await();

          ReservationRequestDTO requestDTO = createReservationRequest(testUsers.get(userIndex),
              numberOfPeople);
          reservationService.createReservation(requestDTO, testUsers.get(userIndex).getEmail());

          successCount.incrementAndGet();
          totalPeopleReserved.addAndGet(numberOfPeople);

        } catch (BadRequestException e) {
          // 예상되는 예외 (수용 인원 부족)
        } catch (Exception e) {
          fail("Unexpected exception: " + e.getMessage());
        } finally {
          completeLatch.countDown();
        }
      });
    }

    startLatch.countDown();
    completeLatch.await();
    executor.shutdown();

    // then
    TimeSlot updatedTimeSlot = timeSlotRepository.findById(timeSlot.getId()).orElseThrow();

    // 실제 예약된 총 인원이 최대 수용 인원을 초과하지 않아야 함
    assertTrue(updatedTimeSlot.getCurrentCapacity() <= updatedTimeSlot.getMaxCapacity(),
        "현재 수용 인원이 최대 수용 인원을 초과했습니다");

    System.out.println("성공한 예약 건수: " + successCount.get());
    System.out.println("예약된 총 인원: " + updatedTimeSlot.getCurrentCapacity());
    System.out.println("최대 수용 인원: " + updatedTimeSlot.getMaxCapacity());
  }

  @Test
  @Order(3)
  @DisplayName("동시 예약 생성과 취소가 발생하는 경우")
  void concurrentReservationAndCancellation() throws InterruptedException {
    // given: 먼저 몇 개의 예약을 생성
    List<Long> existingReservationIds = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      ReservationRequestDTO requestDTO = createReservationRequest(testUsers.get(i), 1);
      var response = reservationService.createReservation(requestDTO, testUsers.get(i).getEmail());
      existingReservationIds.add(response.getReservationId());
    }

    int initialCapacity = timeSlotRepository.findById(timeSlot.getId()).orElseThrow()
        .getCurrentCapacity();

    // when: 동시에 예약 생성과 취소 실행
    CompletableFuture<Void> createFuture = CompletableFuture.runAsync(() -> {
      try {
        for (int i = 3; i < 6; i++) {
          ReservationRequestDTO requestDTO = createReservationRequest(testUsers.get(i), 1);
          reservationService.createReservation(requestDTO, testUsers.get(i).getEmail());
          Thread.sleep(10); // 약간의 지연
        }
      } catch (Exception e) {
        // 예약 실패는 예상 가능
      }
    });

    CompletableFuture<Void> cancelFuture = CompletableFuture.runAsync(() -> {
      try {
        for (Long reservationId : existingReservationIds) {
          User user = testUsers.get(existingReservationIds.indexOf(reservationId));
          reservationService.cancelReservation(reservationId, user.getEmail());
          Thread.sleep(10); // 약간의 지연
        }
      } catch (Exception e) {
        fail("Cancellation should not fail: " + e.getMessage());
      }
    });

    CompletableFuture.allOf(createFuture, cancelFuture).join();

    // then
    TimeSlot finalTimeSlot = timeSlotRepository.findById(timeSlot.getId()).orElseThrow();

    // 데이터 일관성 확인
    int actualReservationCount = reservationRepository.findByTimeSlotId(timeSlot.getId()).size();
    assertEquals(actualReservationCount, finalTimeSlot.getCurrentCapacity(),
        "실제 예약 수와 타임슬롯 현재 수용 인원이 일치하지 않습니다");

    assertTrue(finalTimeSlot.getCurrentCapacity() >= 0, "현재 수용 인원이 음수가 될 수 없습니다");

    assertTrue(finalTimeSlot.getCurrentCapacity() <= finalTimeSlot.getMaxCapacity(),
        "현재 수용 인원이 최대 수용 인원을 초과할 수 없습니다");

    System.out.println("초기 수용 인원: " + initialCapacity);
    System.out.println("최종 수용 인원: " + finalTimeSlot.getCurrentCapacity());
    System.out.println("실제 예약 수: " + actualReservationCount);
  }

  private ReservationRequestDTO createReservationRequest(User user, int numberOfPeople) {
    ReservationRequestDTO requestDTO = new ReservationRequestDTO();
    requestDTO.setUserId(user.getId());
    requestDTO.setBoothId(booth.getId());
    requestDTO.setFestivalId(festival.getId());
    requestDTO.setTimeSlotId(timeSlot.getId());
    requestDTO.setNumberOfPeople(numberOfPeople);
    requestDTO.setPaymentMethod("CARD");
    return requestDTO;
  }
}
