package com.skkutable.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.domain.Role;
import com.skkutable.domain.User;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)   // ★ URL 덮어쓰기 방지
class BoothRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private FestivalRepository festivalRepository;

  @Autowired
  private BoothRepository boothRepository;

  @Autowired
  private UserRepository userRepository;

  private User hostUser1;
  private User hostUser2;
  private User adminUser;
  private Festival festival1;
  private Festival festival2;
  private Festival festival3;
  private Booth booth1;
  private Booth booth2;
  private Booth booth3;
  private Booth booth4;

  @BeforeEach
  void setUp() {
    // 사용자 생성 및 저장
    hostUser1 = User.builder()
        .name("Host User 1")
        .email("host1@test.com")
        .password("password")
        .role(Role.HOST)
        .build();
    hostUser1 = entityManager.persistAndFlush(hostUser1);

    hostUser2 = User.builder()
        .name("Host User 2")
        .email("host2@test.com")
        .password("password")
        .role(Role.HOST)
        .build();
    hostUser2 = entityManager.persistAndFlush(hostUser2);

    adminUser = User.builder()
        .name("Admin User")
        .email("admin@test.com")
        .password("password")
        .role(Role.ADMIN)
        .build();
    adminUser = entityManager.persistAndFlush(adminUser);

    // 축제 생성 및 저장 (ADMIN이 생성)
    festival1 = Festival.builder()
        .name("Spring Festival")
        .description("Spring Festival")
        .location("Seoul")
        .startDate(new Date())
        .endDate(new Date())
        .likeCount(0)
        .build();
    festival1 = entityManager.persistAndFlush(festival1);

    festival2 = Festival.builder()
        .name("Summer Festival")
        .description("Summer Festival")
        .location("Busan")
        .startDate(new Date())
        .endDate(new Date())
        .likeCount(0)
        .build();
    festival2 = entityManager.persistAndFlush(festival2);

    festival3 = Festival.builder()
        .name("Autumn Festival")
        .description("Autumn Festival")
        .location("Daegu")
        .startDate(new Date())
        .endDate(new Date())
        .likeCount(0)
        .build();
    festival3 = entityManager.persistAndFlush(festival3);

    // 부스 생성 및 저장
    booth1 = Booth.builder()
        .name("Food Booth")
        .host("Food Host")
        .location("A-1")
        .description("Delicious food")
        .startDateTime(LocalDateTime.now())
        .endDateTime(LocalDateTime.now().plusHours(4))
        .likeCount(0)
        .build();
    booth1.setFestival(festival1);
    booth1.setCreatedBy(hostUser1);
    booth1 = entityManager.persistAndFlush(booth1);

    booth2 = Booth.builder()
        .name("Game Booth")
        .host("Game Host")
        .location("B-1")
        .description("Fun games")
        .startDateTime(LocalDateTime.now())
        .endDateTime(LocalDateTime.now().plusHours(4))
        .likeCount(0)
        .build();
    booth2.setFestival(festival1);
    booth2.setCreatedBy(hostUser1);
    booth2 = entityManager.persistAndFlush(booth2);

    booth3 = Booth.builder()
        .name("Art Booth")
        .host("Art Host")
        .location("C-1")
        .description("Beautiful art")
        .startDateTime(LocalDateTime.now())
        .endDateTime(LocalDateTime.now().plusHours(4))
        .likeCount(0)
        .build();
    booth3.setFestival(festival2);
    booth3.setCreatedBy(hostUser1);
    booth3 = entityManager.persistAndFlush(booth3);

    booth4 = Booth.builder()
        .name("Music Booth")
        .host("Music Host")
        .location("D-1")
        .description("Live music")
        .startDateTime(LocalDateTime.now())
        .endDateTime(LocalDateTime.now().plusHours(4))
        .likeCount(0)
        .build();
    booth4.setFestival(festival3);
    booth4.setCreatedBy(hostUser2);
    booth4 = entityManager.persistAndFlush(booth4);

    entityManager.clear();
  }

  @Test
  @DisplayName("특정 사용자가 생성한 부스를 조회한다")
  void findByCreatedById_Booth() {
    // when
    List<Booth> hostUser1Booths = boothRepository.findByCreatedById(hostUser1.getId());
    List<Booth> hostUser2Booths = boothRepository.findByCreatedById(hostUser2.getId());
    List<Booth> adminUserBooths = boothRepository.findByCreatedById(adminUser.getId());

    // then
    assertEquals(3, hostUser1Booths.size());
    assertTrue(hostUser1Booths.stream().anyMatch(b -> b.getName().equals("Food Booth")));
    assertTrue(hostUser1Booths.stream().anyMatch(b -> b.getName().equals("Game Booth")));
    assertTrue(hostUser1Booths.stream().anyMatch(b -> b.getName().equals("Art Booth")));

    assertEquals(1, hostUser2Booths.size());
    assertEquals("Music Booth", hostUser2Booths.get(0).getName());

    assertEquals(0, adminUserBooths.size());
  }

  @Test
  @DisplayName("특정 축제의 특정 사용자가 생성한 부스를 조회한다")
  void findByFestivalIdAndCreatedById() {
    // when
    List<Booth> festival1HostUser1Booths = boothRepository.findByFestivalIdAndCreatedById(
        festival1.getId(), hostUser1.getId());
    List<Booth> festival2HostUser1Booths = boothRepository.findByFestivalIdAndCreatedById(
        festival2.getId(), hostUser1.getId());
    List<Booth> festival3HostUser2Booths = boothRepository.findByFestivalIdAndCreatedById(
        festival3.getId(), hostUser2.getId());
    List<Booth> festival1HostUser2Booths = boothRepository.findByFestivalIdAndCreatedById(
        festival1.getId(), hostUser2.getId());

    // then
    assertEquals(2, festival1HostUser1Booths.size());
    assertTrue(festival1HostUser1Booths.stream().anyMatch(b -> b.getName().equals("Food Booth")));
    assertTrue(festival1HostUser1Booths.stream().anyMatch(b -> b.getName().equals("Game Booth")));

    assertEquals(1, festival2HostUser1Booths.size());
    assertEquals("Art Booth", festival2HostUser1Booths.get(0).getName());

    assertEquals(1, festival3HostUser2Booths.size());
    assertEquals("Music Booth", festival3HostUser2Booths.get(0).getName());

    assertEquals(0, festival1HostUser2Booths.size()); // hostUser2는 festival1에 부스가 없음
  }

  @Test
  @DisplayName("존재하지 않는 사용자 ID로 조회 시 빈 리스트를 반환한다")
  void findByCreatedById_NonExistentUser() {
    // when
    List<Booth> booths = boothRepository.findByCreatedById(999L);

    // then
    assertTrue(booths.isEmpty());
  }

  @Test
  @DisplayName("부스는 항상 축제에 속해있다")
  void testBoothAlwaysBelongsToFestival() {
    // when
    List<Booth> allBooths = boothRepository.findAll();

    // then
    assertFalse(allBooths.isEmpty());
    for (Booth booth : allBooths) {
      assertNotNull(booth.getFestival(), "모든 부스는 축제에 속해야 합니다");
    }
  }

  @Test
  @DisplayName("축제와 부스의 연관관계가 올바르게 설정된다")
  void testFestivalBoothRelationship() {
    // when
    Festival foundFestival = entityManager.find(Festival.class, festival1.getId());
    Booth foundBooth = entityManager.find(Booth.class, booth1.getId());

    // then
    assertNotNull(foundFestival);
    assertNotNull(foundBooth);
    assertEquals(foundFestival.getId(), foundBooth.getFestival().getId());
    assertEquals(hostUser1.getId(), foundBooth.getCreatedBy().getId());
  }
}
