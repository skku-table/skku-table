package com.skkutable.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.domain.Role;
import com.skkutable.domain.User;
import com.skkutable.exception.BadRequestException;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.BoothRepository;
import com.skkutable.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoothServiceTest {

  @Mock
  private BoothRepository boothRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private BoothService boothService;

  private User hostUser;
  private Festival festival;
  private Booth booth;

  @BeforeEach
  void setUp() {
    hostUser = User.builder().id(1L).name("Host User").email("host@test.com").role(Role.HOST)
        .build();

    festival = Festival.builder().id(1L).name("Test Festival").build();

    booth = Booth.builder().name("Test Booth").host("Booth Host").location("A-1")
        .description("Test Description").startDateTime(LocalDateTime.now())
        .endDateTime(LocalDateTime.now().plusHours(4)).build();
  }

  @Test
  @DisplayName("부스를 정상적으로 생성하고 생성자 정보를 설정한다")
  void createBooth_Success() {
    // given
    Long festivalId = 1L;
    String userEmail = "host@test.com";
    Booth savedBooth = Booth.builder().id(1L).name(booth.getName()).host(booth.getHost())
        .location(booth.getLocation()).description(booth.getDescription())
        .startDateTime(booth.getStartDateTime()).endDateTime(booth.getEndDateTime()).likeCount(0)
        .build();
    savedBooth.setCreatedBy(hostUser);
    savedBooth.setFestival(festival);

    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(hostUser));
    when(boothRepository.createBooth(eq(festivalId), any(Booth.class))).thenReturn(savedBooth);

    // when
    Booth result = boothService.createBooth(festivalId, booth, userEmail);

    // then
    assertNotNull(result);
    assertEquals("Test Booth", result.getName());
    assertEquals(hostUser, result.getCreatedBy());
    assertEquals(0, result.getLikeCount());

    verify(userRepository, times(1)).findByEmail(userEmail);
    verify(boothRepository, times(1)).createBooth(eq(festivalId), any(Booth.class));
  }

  @Test
  @DisplayName("festivalId가 null일 때 BadRequestException을 발생시킨다")
  void createBooth_NullFestivalId() {
    // given
    String userEmail = "host@test.com";

    // when & then
    BadRequestException exception = assertThrows(BadRequestException.class, () -> {
      boothService.createBooth(null, booth, userEmail);
    });

    assertEquals("Festival ID and Booth data must be provided", exception.getMessage());

    verify(userRepository, never()).findByEmail(anyString());
    verify(boothRepository, never()).createBooth(any(), any());
  }

  @Test
  @DisplayName("booth가 null일 때 BadRequestException을 발생시킨다")
  void createBooth_NullBooth() {
    // given
    Long festivalId = 1L;
    String userEmail = "host@test.com";

    // when & then
    BadRequestException exception = assertThrows(BadRequestException.class, () -> {
      boothService.createBooth(festivalId, null, userEmail);
    });

    assertEquals("Festival ID and Booth data must be provided", exception.getMessage());

    verify(userRepository, never()).findByEmail(anyString());
    verify(boothRepository, never()).createBooth(any(), any());
  }

  @Test
  @DisplayName("존재하지 않는 사용자 이메일로 부스 생성 시 ResourceNotFoundException을 발생시킨다")
  void createBooth_UserNotFound() {
    // given
    Long festivalId = 1L;
    String userEmail = "notfound@test.com";
    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

    // when & then
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      boothService.createBooth(festivalId, booth, userEmail);
    });

    assertEquals("User not found: " + userEmail, exception.getMessage());

    verify(userRepository, times(1)).findByEmail(userEmail);
    verify(boothRepository, never()).createBooth(any(), any());
  }

  @Test
  @DisplayName("Booth의 setCreatedBy 메서드가 정상적으로 작동한다")
  void testSetCreatedBy() {
    // given
    Booth testBooth = Booth.builder().name("Test").build();

    // when
    testBooth.setCreatedBy(hostUser);

    // then
    assertEquals(hostUser, testBooth.getCreatedBy());
  }
}
