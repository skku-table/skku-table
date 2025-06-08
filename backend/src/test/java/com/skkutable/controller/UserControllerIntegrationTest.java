package com.skkutable.controller;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.skkutable.config.SecurityConfig;
import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.dto.HostContentResponseDto;
import com.skkutable.dto.HostContentResponseDto.BoothResponse;
import com.skkutable.dto.HostContentResponseDto.FestivalWithBooths;
import com.skkutable.exception.ForbiddenOperationException;
import com.skkutable.service.CustomUserDetailsService;
import com.skkutable.service.HostContentService;
import com.skkutable.service.UserService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerIntegrationTest {

  @TestConfiguration
  @EnableMethodSecurity(prePostEnabled = true)
  static class TestConfig {
    // 이곳에 필요한 추가 설정을 정의할 수 있습니다.
  }

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private HostContentService hostContentService;

  @MockBean
  CustomUserDetailsService customUserDetailsService;

  private HostContentResponseDto sampleResponse;

  @BeforeEach
  void setUp() {
    // 테스트용 축제와 부스 생성
    Festival festival = Festival.builder().id(1L).name("Spring Festival")
        .description("Spring Festival Description").location("Seoul").likeCount(10).build();

    Booth booth1 = Booth.builder().id(1L).name("Food Booth").host("Food Host").location("A-1")
        .likeCount(5).build();
    booth1.setFestival(festival);

    Booth booth2 = Booth.builder().id(2L).name("Game Booth").host("Game Host").location("B-1")
        .likeCount(8).build();
    booth2.setFestival(festival);

    List<BoothResponse> boothResponses = Arrays.asList(BoothResponse.from(booth1),
        BoothResponse.from(booth2));

    FestivalWithBooths festivalWithBooths = FestivalWithBooths.from(festival,
        Arrays.asList(booth1, booth2));

    sampleResponse = HostContentResponseDto.builder().festivals(List.of(festivalWithBooths))
        .booths(List.of()).build();
  }

  @Test
  @DisplayName("HOST 권한으로 /users/me/booths 접근 시 성공적으로 응답한다")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void getHostContent_AsHost_Success() throws Exception {
    // given
    when(hostContentService.getHostContent("host@test.com")).thenReturn(sampleResponse);

    // when & then
    mockMvc.perform(get("/users/me/booths")).andExpect(status().isOk())
        .andExpect(jsonPath("$.festivals").isArray())
        .andExpect(jsonPath("$.festivals[0].name").value("Spring Festival"))
        .andExpect(jsonPath("$.festivals[0].booths").isArray())
        .andExpect(jsonPath("$.festivals[0].booths[0].name").value("Food Booth"))
        .andExpect(jsonPath("$.festivals[0].booths[1].name").value("Game Booth"))
        .andExpect(jsonPath("$.booths").isArray()).andExpect(jsonPath("$.booths").isEmpty());

    verify(hostContentService, times(1)).getHostContent("host@test.com");
  }

  @Test
  @DisplayName("ADMIN 권한으로 /users/me/booths 접근 시 성공적으로 응답한다")
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void getHostContent_AsAdmin_Success() throws Exception {
    // given
    when(hostContentService.getHostContent("admin@test.com")).thenReturn(sampleResponse);

    // when & then
    mockMvc.perform(get("/users/me/booths")).andExpect(status().isOk())
        .andExpect(jsonPath("$.festivals").isArray()).andExpect(jsonPath("$.booths").isArray());

    verify(hostContentService, times(1)).getHostContent("admin@test.com");
  }

  @Test
  @DisplayName("USER 권한으로 /users/me/booths 접근 시 403 Forbidden을 반환한다")
  @WithMockUser(username = "user@test.com", roles = {"USER"})
  void getHostContent_AsUser_Forbidden() throws Exception {
    // when & then
    mockMvc.perform(get("/users/me/booths")).andExpect(status().isForbidden());

    verify(hostContentService, never()).getHostContent(anyString());
  }

  @Test
  @DisplayName("인증되지 않은 사용자가 /users/me/booths 접근 시 401 Unauthorized를 반환한다")
  void getHostContent_Unauthorized() throws Exception {
    // when & then
    mockMvc.perform(get("/users/me/booths")).andExpect(status().isUnauthorized());

    verify(hostContentService, never()).getHostContent(anyString());
  }

  @Test
  @DisplayName("서비스에서 ForbiddenOperationException 발생 시 403을 반환한다")
  @WithMockUser(username = "fake-host@test.com", roles = {"HOST"})
  void getHostContent_ServiceThrowsForbidden() throws Exception {
    // given
    when(hostContentService.getHostContent("fake-host@test.com")).thenThrow(
        new ForbiddenOperationException("Only hosts can access this resource"));

    // when & then
    mockMvc.perform(get("/users/me/booths")).andExpect(status().isForbidden());

    verify(hostContentService, times(1)).getHostContent("fake-host@test.com");
  }
}
