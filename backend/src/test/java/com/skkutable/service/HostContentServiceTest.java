package com.skkutable.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.domain.Role;
import com.skkutable.domain.User;
import com.skkutable.dto.HostContentResponseDto;
import com.skkutable.exception.ForbiddenOperationException;
import com.skkutable.repository.BoothRepository;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HostContentServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private BoothRepository boothRepository;

    @InjectMocks
    private HostContentService hostContentService;

    private User hostUser;
    private User normalUser;
    private User adminUser;
    private Festival festival1;
    private Festival festival2;
    private Booth booth1;
    private Booth booth2;
    private Booth booth3;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        hostUser = User.builder()
                .id(1L)
                .name("Host User")
                .email("host@test.com")
                .role(Role.HOST)
                .build();

        normalUser = User.builder()
                .id(2L)
                .name("Normal User")
                .email("user@test.com")
                .role(Role.USER)
                .build();

        adminUser = User.builder()
                .id(3L)
                .name("Admin User")
                .email("admin@test.com")
                .role(Role.ADMIN)
                .build();

        festival1 = Festival.builder()
                .id(1L)
                .name("Spring Festival")
                .description("Spring Festival Description")
                .location("Seoul")
                .startDate(new Date())
                .endDate(new Date())
                .likeCount(10)
                .build();

        festival2 = Festival.builder()
                .id(2L)
                .name("Summer Festival")
                .description("Summer Festival Description")
                .location("Busan")
                .startDate(new Date())
                .endDate(new Date())
                .likeCount(20)
                .build();

        booth1 = Booth.builder()
                .id(1L)
                .name("Food Booth")
                .host("Food Host")
                .location("A-1")
                .description("Delicious food")
                .likeCount(5)
                .build();
        booth1.setFestival(festival1);
        booth1.setCreatedBy(hostUser);

        booth2 = Booth.builder()
                .id(2L)
                .name("Game Booth")
                .host("Game Host")
                .location("B-1")
                .description("Fun games")
                .likeCount(8)
                .build();
        booth2.setFestival(festival1);
        booth2.setCreatedBy(hostUser);

        booth3 = Booth.builder()
                .id(3L)
                .name("Art Booth")
                .host("Art Host")
                .location("C-1")
                .description("Beautiful art")
                .likeCount(3)
                .build();
        booth3.setFestival(festival2);
        booth3.setCreatedBy(hostUser);
    }

    @Test
    @DisplayName("호스트가 생성한 부스를 축제별로 그룹화하여 조회한다")
    void getHostContent_Success() {
        // given
        String userEmail = "host@test.com";
        when(userService.getCurrentUser(userEmail)).thenReturn(hostUser);
        when(boothRepository.findByCreatedById(hostUser.getId()))
                .thenReturn(Arrays.asList(booth1, booth2, booth3));

        // when
        HostContentResponseDto result = hostContentService.getHostContent(userEmail);

        // then
        assertNotNull(result);
        assertEquals(2, result.getFestivals().size()); // 2개의 축제에 부스가 분포
        assertEquals(0, result.getBooths().size()); // 모든 부스가 축제에 속함

        // 축제별 부스 확인
        var festivalWithBooths = result.getFestivals();
        
        // 축제별로 부스가 올바르게 그룹화되었는지 확인
        long festival1BoothCount = festivalWithBooths.stream()
                .filter(f -> f.getId().equals(festival1.getId()))
                .mapToLong(f -> f.getBooths().size())
                .sum();
        long festival2BoothCount = festivalWithBooths.stream()
                .filter(f -> f.getId().equals(festival2.getId()))
                .mapToLong(f -> f.getBooths().size())
                .sum();
        
        assertEquals(2, festival1BoothCount); // festival1에는 booth1, booth2
        assertEquals(1, festival2BoothCount); // festival2에는 booth3

        // Mock 호출 검증
        verify(userService, times(1)).getCurrentUser(userEmail);
        verify(boothRepository, times(1)).findByCreatedById(hostUser.getId());
    }

    @Test
    @DisplayName("호스트가 생성한 부스가 없을 때 빈 결과를 반환한다")
    void getHostContent_NoBooths() {
        // given
        String userEmail = "host@test.com";
        when(userService.getCurrentUser(userEmail)).thenReturn(hostUser);
        when(boothRepository.findByCreatedById(hostUser.getId()))
                .thenReturn(List.of());

        // when
        HostContentResponseDto result = hostContentService.getHostContent(userEmail);

        // then
        assertNotNull(result);
        assertEquals(0, result.getFestivals().size());
        assertEquals(0, result.getBooths().size());
    }

    @Test
    @DisplayName("USER 권한으로 접근 시 ForbiddenOperationException을 발생시킨다")
    void getHostContent_ForbiddenForNormalUser() {
        // given
        String userEmail = "user@test.com";
        when(userService.getCurrentUser(userEmail)).thenReturn(normalUser);

        // when & then
        assertThrows(ForbiddenOperationException.class, () -> {
            hostContentService.getHostContent(userEmail);
        });

        // Repository는 호출되지 않아야 함
        verify(boothRepository, never()).findByCreatedById(anyLong());
    }

    @Test
    @DisplayName("ADMIN 권한으로 접근 시 정상적으로 조회한다")
    void getHostContent_AllowedForAdmin() {
        // given
        String userEmail = "admin@test.com";
        when(userService.getCurrentUser(userEmail)).thenReturn(adminUser);
        when(boothRepository.findByCreatedById(adminUser.getId()))
                .thenReturn(List.of());

        // when
        HostContentResponseDto result = hostContentService.getHostContent(userEmail);

        // then
        assertNotNull(result);
        assertEquals(0, result.getFestivals().size());
        assertEquals(0, result.getBooths().size());
    }

    @Test
    @DisplayName("특정 축제의 호스트가 생성한 부스만 조회한다")
    void getHostBoothsByFestival_Success() {
        // given
        String userEmail = "host@test.com";
        Long festivalId = 1L;
        when(userService.getCurrentUser(userEmail)).thenReturn(hostUser);
        when(boothRepository.findByFestivalIdAndCreatedById(festivalId, hostUser.getId()))
                .thenReturn(Arrays.asList(booth1, booth2));

        // when
        var result = hostContentService.getHostBoothsByFestival(userEmail, festivalId);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Food Booth", result.get(0).getName());
        assertEquals("Game Booth", result.get(1).getName());

        verify(userService, times(1)).getCurrentUser(userEmail);
        verify(boothRepository, times(1)).findByFestivalIdAndCreatedById(festivalId, hostUser.getId());
    }

    @Test
    @DisplayName("USER 권한으로 특정 축제 부스 조회 시 ForbiddenOperationException을 발생시킨다")
    void getHostBoothsByFestival_ForbiddenForNormalUser() {
        // given
        String userEmail = "user@test.com";
        Long festivalId = 1L;
        when(userService.getCurrentUser(userEmail)).thenReturn(normalUser);

        // when & then
        assertThrows(ForbiddenOperationException.class, () -> {
            hostContentService.getHostBoothsByFestival(userEmail, festivalId);
        });

        verify(boothRepository, never()).findByFestivalIdAndCreatedById(anyLong(), anyLong());
    }
}
