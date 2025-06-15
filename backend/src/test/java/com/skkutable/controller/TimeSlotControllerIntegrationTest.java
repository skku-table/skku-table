package com.skkutable.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skkutable.config.SecurityConfig;
import com.skkutable.domain.TimeSlotStatus;
import com.skkutable.dto.TimeSlotCreateDto;
import com.skkutable.dto.TimeSlotResponseDto;
import com.skkutable.dto.TimeSlotUpdateDto;
import com.skkutable.exception.BadRequestException;
import com.skkutable.exception.ForbiddenOperationException;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.service.CustomUserDetailsService;
import com.skkutable.service.TimeSlotService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TimeSlotController.class)
@Import(SecurityConfig.class)
class TimeSlotControllerIntegrationTest {

  @TestConfiguration
  @EnableMethodSecurity(prePostEnabled = true)
  static class TestConfig {

  }

  @Autowired
  private MockMvc mockMvc;

  @org.springframework.boot.test.mock.mockito.MockBean
  private TimeSlotService timeSlotService;

  @org.springframework.boot.test.mock.mockito.MockBean
  private CustomUserDetailsService customUserDetailsService;

  @Autowired
  private ObjectMapper objectMapper;

  private TimeSlotCreateDto createDto;
  private TimeSlotUpdateDto updateDto;
  private TimeSlotResponseDto responseDto;

  @BeforeEach
  void setUp() {
    createDto = new TimeSlotCreateDto();
    createDto.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
    createDto.setEndTime(LocalDateTime.of(2024, 1, 1, 11, 0));
    createDto.setMaxCapacity(10);

    updateDto = new TimeSlotUpdateDto();
    updateDto.setMaxCapacity(15);
    updateDto.setStatus(TimeSlotStatus.CLOSED);

    responseDto = TimeSlotResponseDto.builder().id(1L).boothId(1L).boothName("Test Booth")
        .startTime("2024-01-01T10:00").endTime("2024-01-01T11:00").maxCapacity(10)
        .currentCapacity(3).availableCapacity(7).status(TimeSlotStatus.AVAILABLE).build();
  }

  @Test
  @DisplayName("HOST 권한으로 타임슬롯 생성 성공")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void createTimeSlot_Success_Host() throws Exception {
    // given
    when(timeSlotService.createTimeSlot(eq(1L), any(TimeSlotCreateDto.class),
        eq("host@test.com"))).thenReturn(responseDto);

    // when & then
    mockMvc.perform(post("/booths/1/timeslots").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.boothId").value(1L))
        .andExpect(jsonPath("$.boothName").value("Test Booth"))
        .andExpect(jsonPath("$.maxCapacity").value(10))
        .andExpect(jsonPath("$.currentCapacity").value(3))
        .andExpect(jsonPath("$.status").value("AVAILABLE"));

    verify(timeSlotService).createTimeSlot(eq(1L), any(TimeSlotCreateDto.class),
        eq("host@test.com"));
  }

  @Test
  @DisplayName("ADMIN 권한으로 타임슬롯 생성 성공")
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void createTimeSlot_Success_Admin() throws Exception {
    // given
    when(timeSlotService.createTimeSlot(eq(1L), any(TimeSlotCreateDto.class),
        eq("admin@test.com"))).thenReturn(responseDto);

    // when & then
    mockMvc.perform(post("/booths/1/timeslots").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L));

    verify(timeSlotService).createTimeSlot(eq(1L), any(TimeSlotCreateDto.class),
        eq("admin@test.com"));
  }

  @Test
  @DisplayName("USER 권한으로 타임슬롯 생성 시 403 Forbidden")
  @WithMockUser(username = "user@test.com", roles = {"USER"})
  void createTimeSlot_Forbidden_User() throws Exception {
    // when & then
    mockMvc.perform(post("/booths/1/timeslots").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto))).andExpect(status().isForbidden());

    verify(timeSlotService, never()).createTimeSlot(any(), any(), any());
  }

  @Test
  @DisplayName("인증되지 않은 사용자의 타임슬롯 생성 시 401 Unauthorized")
  void createTimeSlot_Unauthorized() throws Exception {
    // when & then
    mockMvc.perform(post("/booths/1/timeslots").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto))).andExpect(status().isUnauthorized());

    verify(timeSlotService, never()).createTimeSlot(any(), any(), any());
  }

  @Test
  @DisplayName("잘못된 입력값으로 타임슬롯 생성 시 400 Bad Request")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void createTimeSlot_BadRequest_InvalidInput() throws Exception {
    // given
    createDto.setStartTime(null); // 필수값 누락

    // when & then
    mockMvc.perform(post("/booths/1/timeslots").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto))).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("서비스에서 BadRequestException 발생 시 400 Bad Request")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void createTimeSlot_BadRequest_ServiceException() throws Exception {
    // given
    when(timeSlotService.createTimeSlot(eq(1L), any(TimeSlotCreateDto.class),
        eq("host@test.com"))).thenThrow(new BadRequestException("시작 시간은 종료 시간보다 앞서야 합니다"));

    // when & then
    mockMvc.perform(post("/booths/1/timeslots").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto))).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("타임슬롯 수정 성공")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void updateTimeSlot_Success() throws Exception {
    // given
    responseDto = TimeSlotResponseDto.builder().id(1L).boothId(1L).maxCapacity(15)
        .status(TimeSlotStatus.CLOSED).build();

    when(timeSlotService.updateTimeSlot(eq(1L), eq(1L), any(TimeSlotUpdateDto.class),
        eq("host@test.com"))).thenReturn(responseDto);

    // when & then
    mockMvc.perform(patch("/booths/1/timeslots/1").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.maxCapacity").value(15))
        .andExpect(jsonPath("$.status").value("CLOSED"));

    verify(timeSlotService).updateTimeSlot(eq(1L), eq(1L), any(TimeSlotUpdateDto.class),
        eq("host@test.com"));
  }

  @Test
  @DisplayName("권한 없는 사용자의 타임슬롯 수정 시 ForbiddenOperationException")
  @WithMockUser(username = "other@test.com", roles = {"HOST"})
  void updateTimeSlot_ForbiddenOperation() throws Exception {
    // given
    when(timeSlotService.updateTimeSlot(eq(1L), eq(1L), any(TimeSlotUpdateDto.class),
        eq("other@test.com"))).thenThrow(
        new ForbiddenOperationException("자신이 생성한 부스의 타임슬롯만 수정할 수 있습니다"));

    // when & then
    mockMvc.perform(patch("/booths/1/timeslots/1").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("타임슬롯 삭제 성공")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void deleteTimeSlot_Success() throws Exception {
    // given
    doNothing().when(timeSlotService).deleteTimeSlot(1L, 1L, "host@test.com");

    // when & then
    mockMvc.perform(delete("/booths/1/timeslots/1")).andExpect(status().isNoContent());

    verify(timeSlotService).deleteTimeSlot(1L, 1L, "host@test.com");
  }

  @Test
  @DisplayName("존재하지 않는 타임슬롯 삭제 시 404 Not Found")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void deleteTimeSlot_NotFound() throws Exception {
    // given
    doThrow(new ResourceNotFoundException("TimeSlot not found with id 1 in booth 1")).when(
        timeSlotService).deleteTimeSlot(1L, 1L, "host@test.com");

    // when & then
    mockMvc.perform(delete("/booths/1/timeslots/1")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("부스의 모든 타임슬롯 조회 성공")
  void getTimeSlots_Success() throws Exception {
    // given
    List<TimeSlotResponseDto> timeSlots = Arrays.asList(responseDto);
    when(timeSlotService.getTimeSlotsByBooth(1L)).thenReturn(timeSlots);

    // when & then
    mockMvc.perform(get("/booths/1/timeslots")).andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].boothId").value(1L))
        .andExpect(jsonPath("$[0].boothName").value("Test Booth"));

    verify(timeSlotService).getTimeSlotsByBooth(1L);
  }

  @Test
  @DisplayName("부스의 예약 가능한 타임슬롯 조회 성공")
  void getAvailableTimeSlots_Success() throws Exception {
    // given
    List<TimeSlotResponseDto> timeSlots = Arrays.asList(responseDto);
    when(timeSlotService.getAvailableTimeSlots(1L)).thenReturn(timeSlots);

    // when & then
    mockMvc.perform(get("/booths/1/timeslots/available")).andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].status").value("AVAILABLE"));

    verify(timeSlotService).getAvailableTimeSlots(1L);
  }

  @Test
  @DisplayName("존재하지 않는 부스의 타임슬롯 조회 시 404 Not Found")
  void getTimeSlots_BoothNotFound() throws Exception {
    // given
    when(timeSlotService.getTimeSlotsByBooth(999L)).thenThrow(
        new ResourceNotFoundException("Booth not found: 999"));

    // when & then
    mockMvc.perform(get("/booths/999/timeslots")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("유효성 검증 실패 시 적절한 에러 응답")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void createTimeSlot_ValidationError() throws Exception {
    // given
    TimeSlotCreateDto invalidDto = new TimeSlotCreateDto();
    invalidDto.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
    invalidDto.setEndTime(LocalDateTime.of(2024, 1, 1, 11, 0));
    invalidDto.setMaxCapacity(-1); // 음수값

    // when & then
    mockMvc.perform(post("/booths/1/timeslots").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidDto))).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("JSON 형식이 잘못된 경우 400 Bad Request")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void createTimeSlot_InvalidJson() throws Exception {
    // when & then
    mockMvc.perform(post("/booths/1/timeslots").contentType(MediaType.APPLICATION_JSON)
        .content("{ invalid json }")).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Content-Type이 누락된 경우 415 Unsupported Media Type")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void createTimeSlot_UnsupportedMediaType() throws Exception {
    // when & then
    mockMvc.perform(post("/booths/1/timeslots").content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isUnsupportedMediaType());
  }

  @Test
  @DisplayName("HTTP Method가 지원되지 않는 경우 405 Method Not Allowed")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void methodNotAllowed() throws Exception {
    // when & then
    mockMvc.perform(put("/booths/1/timeslots").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isMethodNotAllowed());
  }
} 