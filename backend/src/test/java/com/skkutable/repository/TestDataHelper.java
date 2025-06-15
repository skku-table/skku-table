package com.skkutable.repository;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.domain.Role;
import com.skkutable.domain.TimeSlot;
import com.skkutable.domain.TimeSlotStatus;
import com.skkutable.domain.User;
import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 테스트 데이터 생성을 위한 헬퍼 클래스
 */
@Component
public class TestDataHelper {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FestivalRepository festivalRepository;

  @Autowired
  private BoothRepository boothRepository;

  @Autowired
  private TimeSlotRepository timeSlotRepository;

  /**
   * 테스트용 사용자 생성
   */
  public User createUser(String name, String email, Role role) {
    User user = User.builder().name(name).email(email).password("password").role(role).build();
    return userRepository.save(user);
  }

  /**
   * 테스트용 축제 생성
   */
  public Festival createFestival(String name, Date startDate, Date endDate) {
    Festival festival = Festival.builder().name(name).startDate(startDate).endDate(endDate)
        .location("Test Location").description("Test Description").likeCount(0).build();
    return festivalRepository.save(festival);
  }

  /**
   * 테스트용 부스 생성
   */
  public Booth createBooth(Festival festival, User host, String name) {
    Booth booth = Booth.builder().name(name).description("Test Description").host(host.getName())
        .location("Test Location").startDateTime(LocalDateTime.now().minusHours(1))
        .endDateTime(LocalDateTime.now().plusHours(5))
        .reservationOpenTime(LocalDateTime.now().minusHours(1)).likeCount(0).build();
    booth.setFestival(festival);
    booth.setCreatedBy(host);
    return boothRepository.save(booth);
  }

  /**
   * 테스트용 타임슬롯 생성
   */
  public TimeSlot createTimeSlot(Booth booth, int maxCapacity) {
    TimeSlot timeSlot = TimeSlot.builder().booth(booth).startTime(LocalDateTime.now().plusHours(1))
        .endTime(LocalDateTime.now().plusHours(2)).maxCapacity(maxCapacity).currentCapacity(0)
        .status(TimeSlotStatus.AVAILABLE).build();
    return timeSlotRepository.save(timeSlot);
  }
}
