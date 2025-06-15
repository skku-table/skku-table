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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skkutable.config.SecurityConfig;
import com.skkutable.dto.ReservationRequestDTO;
import com.skkutable.dto.ReservationResponseDTO;
import com.skkutable.exception.BadRequestException;
import com.skkutable.exception.ForbiddenOperationException;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.service.CustomUserDetailsService;
import com.skkutable.service.ReservationServiceV2;
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

@WebMvcTest(ReservationControllerV2.class)
@Import(SecurityConfig.class)
class ReservationControllerV2IntegrationTest {

  @TestConfiguration
  @EnableMethodSecurity(prePostEnabled = true)
  static class TestConfig {

  }

  @Autowired
  private MockMvc mockMvc;

  @org.springframework.boot.test.mock.mockito.MockBean
  private ReservationServiceV2 reservationService;

  @org.springframework.boot.test.mock.mockito.MockBean
  private CustomUserDetailsService customUserDetailsService;

  @Autowired
  private ObjectMapper objectMapper;

  private ReservationRequestDTO requestDTO;
  private ReservationResponseDTO responseDTO;

  @BeforeEach
  void setUp() {
    requestDTO = new ReservationRequestDTO();
    requestDTO.setUserId(1L);
    requestDTO.setBoothId(1L);
    requestDTO.setFestivalId(1L);
    requestDTO.setTimeSlotId(1L);
    requestDTO.setNumberOfPeople(3);
    requestDTO.setPaymentMethod("CARD");

    responseDTO = new ReservationResponseDTO();
    responseDTO.setReservationId(1L);
    responseDTO.setUserId(1L);
    responseDTO.setUserName("Test User");
    responseDTO.setBoothId(1L);
    responseDTO.setBoothName("Test Booth");
    responseDTO.setFestivalId(1L);
    responseDTO.setFestivalName("Test Festival");
    responseDTO.setNumberOfPeople(3);
    responseDTO.setPaymentMethod("CARD");
    responseDTO.setTimeSlotId(1L);
    responseDTO.setTimeSlotStartTime(LocalDateTime.of(2024, 1, 1, 14, 0));
    responseDTO.setTimeSlotEndTime(LocalDateTime.of(2024, 1, 1, 15, 0));
    responseDTO.setCreatedAt(LocalDateTime.now());
  }

  @Test
  @DisplayName("사용자가 예약을 성공적으로 생성한다")
  @WithMockUser(username = "user@test.com", roles = {"USER"})
  void createReservation_Success() throws Exception {
    // given
    when(reservationService.createReservation(any(ReservationRequestDTO.class),
        eq("user@test.com"))).thenReturn(responseDTO);

    // when & then
    mockMvc.perform(post("/v2/reservations").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.reservationId").value(1L)).andExpect(jsonPath("$.userId").value(1L))
        .andExpect(jsonPath("$.userName").value("Test User"))
        .andExpect(jsonPath("$.boothId").value(1L))
        .andExpect(jsonPath("$.boothName").value("Test Booth"))
        .andExpect(jsonPath("$.numberOfPeople").value(3))
        .andExpect(jsonPath("$.paymentMethod").value("CARD"))
        .andExpect(jsonPath("$.timeSlotId").value(1L));

    verify(reservationService).createReservation(any(ReservationRequestDTO.class),
        eq("user@test.com"));
  }

  @Test
  @DisplayName("HOST가 예약을 성공적으로 생성한다")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void createReservation_Success_Host() throws Exception {
    // given
    when(reservationService.createReservation(any(ReservationRequestDTO.class),
        eq("host@test.com"))).thenReturn(responseDTO);

    // when & then
    mockMvc.perform(post("/v2/reservations").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.reservationId").value(1L));

    verify(reservationService).createReservation(any(ReservationRequestDTO.class),
        eq("host@test.com"));
  }

  @Test
  @DisplayName("ADMIN이 예약을 성공적으로 생성한다")
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void createReservation_Success_Admin() throws Exception {
    // given
    when(reservationService.createReservation(any(ReservationRequestDTO.class),
        eq("admin@test.com"))).thenReturn(responseDTO);

    // when & then
    mockMvc.perform(post("/v2/reservations").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.reservationId").value(1L));

    verify(reservationService).createReservation(any(ReservationRequestDTO.class),
        eq("admin@test.com"));
  }

  @Test
  @DisplayName("인증되지 않은 사용자의 예약 생성 시 401 Unauthorized")
  void createReservation_Unauthorized() throws Exception {
    // when & then
    mockMvc.perform(post("/v2/reservations").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isUnauthorized());

    verify(reservationService, never()).createReservation(any(), any());
  }

  @Test
  @DisplayName("잘못된 입력값으로 예약 생성 시 400 Bad Request")
  @WithMockUser(username = "user@test.com")
  void createReservation_BadRequest_InvalidInput() throws Exception {
    // given
    requestDTO.setUserId(null); // 필수값 누락

    // when & then
    mockMvc.perform(post("/v2/reservations").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("본인이 아닌 사용자의 예약 생성 시 403 Forbidden")
  @WithMockUser(username = "user@test.com")
  void createReservation_ForbiddenUser() throws Exception {
    // given
    when(reservationService.createReservation(any(ReservationRequestDTO.class),
        eq("user@test.com"))).thenThrow(new ForbiddenOperationException("본인의 예약만 생성할 수 있습니다"));

    // when & then
    mockMvc.perform(post("/v2/reservations").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("예약 가능 시간 이전에 예약 생성 시 400 Bad Request")
  @WithMockUser(username = "user@test.com")
  void createReservation_BeforeOpenTime() throws Exception {
    // given
    when(reservationService.createReservation(any(ReservationRequestDTO.class),
        eq("user@test.com"))).thenThrow(new BadRequestException("예약 가능 시간이 아닙니다"));

    // when & then
    mockMvc.perform(post("/v2/reservations").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("타임슬롯 수용 인원 초과 시 400 Bad Request")
  @WithMockUser(username = "user@test.com")
  void createReservation_ExceedsCapacity() throws Exception {
    // given
    when(reservationService.createReservation(any(ReservationRequestDTO.class),
        eq("user@test.com"))).thenThrow(new BadRequestException("남은 자리가 부족합니다. 현재 가능 인원: 2"));

    // when & then
    mockMvc.perform(post("/v2/reservations").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("존재하지 않는 리소스로 예약 생성 시 404 Not Found")
  @WithMockUser(username = "user@test.com")
  void createReservation_ResourceNotFound() throws Exception {
    // given
    when(reservationService.createReservation(any(ReservationRequestDTO.class),
        eq("user@test.com"))).thenThrow(new ResourceNotFoundException("TimeSlot not found: 1"));

    // when & then
    mockMvc.perform(post("/v2/reservations").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("예약을 성공적으로 취소한다")
  @WithMockUser(username = "user@test.com")
  void cancelReservation_Success() throws Exception {
    // given
    doNothing().when(reservationService).cancelReservation(1L, "user@test.com");

    // when & then
    mockMvc.perform(delete("/v2/reservations/1")).andExpect(status().isNoContent());

    verify(reservationService).cancelReservation(1L, "user@test.com");
  }

  @Test
  @DisplayName("본인의 예약이 아닌 경우 취소 시 403 Forbidden")
  @WithMockUser(username = "user@test.com")
  void cancelReservation_NotOwnReservation() throws Exception {
    // given
    doThrow(new ForbiddenOperationException("본인의 예약만 취소할 수 있습니다")).when(reservationService)
        .cancelReservation(1L, "user@test.com");

    // when & then
    mockMvc.perform(delete("/v2/reservations/1")).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("존재하지 않는 예약 취소 시 404 Not Found")
  @WithMockUser(username = "user@test.com")
  void cancelReservation_NotFound() throws Exception {
    // given
    doThrow(new ResourceNotFoundException("Reservation not found: 999")).when(reservationService)
        .cancelReservation(999L, "user@test.com");

    // when & then
    mockMvc.perform(delete("/v2/reservations/999")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("내 예약 목록을 성공적으로 조회한다")
  @WithMockUser(username = "user@test.com")
  void getMyReservations_Success() throws Exception {
    // given
    List<ReservationResponseDTO> reservations = Arrays.asList(responseDTO);
    when(reservationService.getUserReservations("user@test.com")).thenReturn(reservations);

    // when & then
    mockMvc.perform(get("/v2/reservations/my")).andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$[0].reservationId").value(1L))
        .andExpect(jsonPath("$[0].userName").value("Test User"))
        .andExpect(jsonPath("$[0].boothName").value("Test Booth"))
        .andExpect(jsonPath("$[0].timeSlotId").value(1L));

    verify(reservationService).getUserReservations("user@test.com");
  }

  @Test
  @DisplayName("HOST가 자신의 부스 타임슬롯 예약 현황을 조회한다")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void getTimeSlotReservations_Host_Success() throws Exception {
    // given
    List<ReservationResponseDTO> reservations = Arrays.asList(responseDTO);
    when(reservationService.getTimeSlotReservations(1L, "host@test.com")).thenReturn(reservations);

    // when & then
    mockMvc.perform(get("/v2/reservations/timeslots/1")).andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$[0].reservationId").value(1L))
        .andExpect(jsonPath("$[0].userName").value("Test User"));

    verify(reservationService).getTimeSlotReservations(1L, "host@test.com");
  }

  @Test
  @DisplayName("ADMIN이 모든 부스 타임슬롯 예약 현황을 조회한다")
  @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
  void getTimeSlotReservations_Admin_Success() throws Exception {
    // given
    List<ReservationResponseDTO> reservations = Arrays.asList(responseDTO);
    when(reservationService.getTimeSlotReservations(1L, "admin@test.com")).thenReturn(reservations);

    // when & then
    mockMvc.perform(get("/v2/reservations/timeslots/1")).andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$[0].reservationId").value(1L));

    verify(reservationService).getTimeSlotReservations(1L, "admin@test.com");
  }

  @Test
  @DisplayName("USER가 타임슬롯 예약 현황 조회 시 403 Forbidden")
  @WithMockUser(username = "user@test.com", roles = {"USER"})
  void getTimeSlotReservations_User_Forbidden() throws Exception {
    // when & then
    mockMvc.perform(get("/v2/reservations/timeslots/1")).andExpect(status().isForbidden());

    verify(reservationService, never()).getTimeSlotReservations(any(), any());
  }

  @Test
  @DisplayName("HOST가 다른 사용자의 부스 타임슬롯 조회 시 403 Forbidden")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void getTimeSlotReservations_Host_OtherBooth() throws Exception {
    // given
    when(reservationService.getTimeSlotReservations(1L, "host@test.com")).thenThrow(
        new ForbiddenOperationException("자신의 부스 예약만 조회할 수 있습니다"));

    // when & then
    mockMvc.perform(get("/v2/reservations/timeslots/1")).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("존재하지 않는 타임슬롯의 예약 현황 조회 시 404 Not Found")
  @WithMockUser(username = "host@test.com", roles = {"HOST"})
  void getTimeSlotReservations_TimeSlotNotFound() throws Exception {
    // given
    when(reservationService.getTimeSlotReservations(999L, "host@test.com")).thenThrow(
        new ResourceNotFoundException("TimeSlot not found: 999"));

    // when & then
    mockMvc.perform(get("/v2/reservations/timeslots/999")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("잘못된 JSON 형식으로 예약 생성 시 400 Bad Request")
  @WithMockUser(username = "user@test.com")
  void createReservation_InvalidJson() throws Exception {
    // when & then
    mockMvc.perform(post("/v2/reservations").contentType(MediaType.APPLICATION_JSON)
        .content("{ invalid json }")).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Content-Type이 누락된 경우 415 Unsupported Media Type")
  @WithMockUser(username = "user@test.com")
  void createReservation_UnsupportedMediaType() throws Exception {
    // when & then
    mockMvc.perform(
            post("/v2/reservations").content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isUnsupportedMediaType());
  }

  @Test
  @DisplayName("인원수가 0 이하인 경우 400 Bad Request")
  @WithMockUser(username = "user@test.com")
  void createReservation_InvalidNumberOfPeople() throws Exception {
    // given
    requestDTO.setNumberOfPeople(0);

    // when & then
    mockMvc.perform(post("/v2/reservations").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("필수 필드가 누락된 경우 400 Bad Request")
  @WithMockUser(username = "user@test.com")
  void createReservation_MissingRequiredFields() throws Exception {
    // given
    ReservationRequestDTO incompleteDTO = new ReservationRequestDTO();
    incompleteDTO.setUserId(1L);
    // 다른 필수 필드들은 누락

    // when & then
    mockMvc.perform(post("/v2/reservations").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(incompleteDTO)))
        .andExpect(status().isBadRequest());
  }
} 