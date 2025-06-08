package com.skkutable.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.skkutable.domain.Festival;
import com.skkutable.domain.Role;
import com.skkutable.domain.User;
import com.skkutable.exception.ForbiddenOperationException;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.FestivalRepository;
import com.skkutable.repository.UserRepository;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FestivalServiceTest {

    @Mock
    private FestivalRepository festivalRepository;

    @Mock
    private BoothService boothService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FestivalService festivalService;

    private User adminUser;
    private User hostUser;
    private User normalUser;
    private Festival festival;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(1L)
                .name("Admin User")
                .email("admin@test.com")
                .role(Role.ADMIN)
                .build();

        hostUser = User.builder()
                .id(2L)
                .name("Host User")
                .email("host@test.com")
                .role(Role.HOST)
                .build();

        normalUser = User.builder()
                .id(3L)
                .name("Normal User")
                .email("user@test.com")
                .role(Role.USER)
                .build();

        festival = Festival.builder()
                .name("Test Festival")
                .description("Test Description")
                .location("Seoul")
                .startDate(new Date())
                .endDate(new Date())
                .build();
    }

    @Test
    @DisplayName("ADMIN 권한으로 축제를 정상적으로 생성한다")
    void createFestival_Success_AsAdmin() {
        // given
        String userEmail = "admin@test.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(adminUser));
        when(festivalRepository.existsByName(festival.getName())).thenReturn(false);
        when(festivalRepository.save(any(Festival.class))).thenAnswer(invocation -> {
            Festival savedFestival = invocation.getArgument(0);
            savedFestival.setLikeCount(0); // save 시 기본값 설정
            return savedFestival;
        });

        // when
        Festival result = festivalService.createFestival(festival, userEmail);

        // then
        assertNotNull(result);
        assertEquals("Test Festival", result.getName());
        assertEquals(0, result.getLikeCount());

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(festivalRepository, times(1)).existsByName(festival.getName());
        verify(festivalRepository, times(1)).save(festival);
    }

    @Test
    @DisplayName("HOST 권한으로 축제 생성 시 ForbiddenOperationException을 발생시킨다")
    void createFestival_ForbiddenForHost() {
        // given
        String userEmail = "host@test.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(hostUser));

        // when & then
        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class, () -> {
            festivalService.createFestival(festival, userEmail);
        });

        assertEquals("Only administrators can create festivals", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(festivalRepository, never()).existsByName(anyString());
        verify(festivalRepository, never()).save(any());
    }

    @Test
    @DisplayName("USER 권한으로 축제 생성 시 ForbiddenOperationException을 발생시킨다")
    void createFestival_ForbiddenForUser() {
        // given
        String userEmail = "user@test.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(normalUser));

        // when & then
        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class, () -> {
            festivalService.createFestival(festival, userEmail);
        });

        assertEquals("Only administrators can create festivals", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(festivalRepository, never()).existsByName(anyString());
        verify(festivalRepository, never()).save(any());
    }

    @Test
    @DisplayName("중복된 이름의 축제가 있을 때 IllegalArgumentException을 발생시킨다")
    void createFestival_DuplicateName() {
        // given
        String userEmail = "admin@test.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(adminUser));
        when(festivalRepository.existsByName(festival.getName())).thenReturn(true);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            festivalService.createFestival(festival, userEmail);
        });

        assertEquals("이미 같은 이름의 축제가 존재합니다: " + festival.getName(), exception.getMessage());

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(festivalRepository, times(1)).existsByName(festival.getName());
        verify(festivalRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 이메일로 축제 생성 시 ResourceNotFoundException을 발생시킨다")
    void createFestival_UserNotFound() {
        // given
        String userEmail = "notfound@test.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            festivalService.createFestival(festival, userEmail);
        });

        assertEquals("User not found: " + userEmail, exception.getMessage());

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(festivalRepository, never()).existsByName(anyString());
        verify(festivalRepository, never()).save(any());
    }
}
