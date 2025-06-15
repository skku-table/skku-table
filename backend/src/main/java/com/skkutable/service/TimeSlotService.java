package com.skkutable.service;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Role;
import com.skkutable.domain.TimeSlot;
import com.skkutable.domain.TimeSlotStatus;
import com.skkutable.domain.User;
import com.skkutable.dto.TimeSlotCreateDto;
import com.skkutable.dto.TimeSlotResponseDto;
import com.skkutable.dto.TimeSlotUpdateDto;
import com.skkutable.exception.BadRequestException;
import com.skkutable.exception.ForbiddenOperationException;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.TimeSlotRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TimeSlotService {

  private final TimeSlotRepository timeSlotRepository;
  private final BoothService boothService;
  private final UserService userService;

  // TimeSlot 생성 (HOST만 가능)
  public TimeSlotResponseDto createTimeSlot(Long boothId, TimeSlotCreateDto dto, String userEmail) {
    User user = userService.getCurrentUser(userEmail);
    Booth booth = boothService.findBoothById(boothId);
    
    // 권한 검증: HOST는 자신이 만든 부스만, ADMIN은 모든 부스
    if (user.getRole() == Role.HOST && !booth.getCreatedBy().getId().equals(user.getId())) {
      throw new ForbiddenOperationException("자신이 생성한 부스의 타임슬롯만 생성할 수 있습니다");
    }
    
    // 시간 유효성 검증
    validateTimeSlot(dto.getStartTime(), dto.getEndTime(), booth);
    
    // 중복 시간대 검증
    if (timeSlotRepository.existsByBoothIdAndStartTimeAndEndTime(
        boothId, dto.getStartTime(), dto.getEndTime())) {
      throw new BadRequestException("이미 동일한 시간대의 타임슬롯이 존재합니다");
    }
    
    TimeSlot timeSlot = TimeSlot.builder()
        .booth(booth)
        .startTime(dto.getStartTime())
        .endTime(dto.getEndTime())
        .maxCapacity(dto.getMaxCapacity())
        .currentCapacity(0)
        .status(TimeSlotStatus.AVAILABLE)
        .build();
    
    TimeSlot saved = timeSlotRepository.save(timeSlot);
    return TimeSlotResponseDto.from(saved);
  }

  // TimeSlot 수정 (HOST만 가능)
  public TimeSlotResponseDto updateTimeSlot(Long boothId, Long timeSlotId, 
                                            TimeSlotUpdateDto dto, String userEmail) {
    User user = userService.getCurrentUser(userEmail);
    Booth booth = boothService.findBoothById(boothId);
    TimeSlot timeSlot = findTimeSlotByIdAndBoothId(timeSlotId, boothId);
    
    // 권한 검증
    if (user.getRole() == Role.HOST && !booth.getCreatedBy().getId().equals(user.getId())) {
      throw new ForbiddenOperationException("자신이 생성한 부스의 타임슬롯만 수정할 수 있습니다");
    }
    
    // 예약이 있는 경우 시간 변경 불가
    if (timeSlot.getCurrentCapacity() > 0 && 
        (dto.getStartTime() != null || dto.getEndTime() != null)) {
      throw new BadRequestException("예약이 존재하는 타임슬롯의 시간은 변경할 수 없습니다");
    }
    
    // 업데이트 처리
    if (dto.getStartTime() != null) {
      timeSlot.setStartTime(dto.getStartTime());
    }
    if (dto.getEndTime() != null) {
      timeSlot.setEndTime(dto.getEndTime());
    }
    if (dto.getMaxCapacity() != null) {
      if (dto.getMaxCapacity() < timeSlot.getCurrentCapacity()) {
        throw new BadRequestException("최대 수용 인원은 현재 예약 인원보다 작을 수 없습니다");
      }
      timeSlot.setMaxCapacity(dto.getMaxCapacity());
    }
    if (dto.getStatus() != null) {
      timeSlot.setStatus(dto.getStatus());
    }
    
    return TimeSlotResponseDto.from(timeSlot);
  }

  // TimeSlot 삭제 (HOST만 가능)
  public void deleteTimeSlot(Long boothId, Long timeSlotId, String userEmail) {
    User user = userService.getCurrentUser(userEmail);
    Booth booth = boothService.findBoothById(boothId);
    TimeSlot timeSlot = findTimeSlotByIdAndBoothId(timeSlotId, boothId);
    
    // 권한 검증
    if (user.getRole() == Role.HOST && !booth.getCreatedBy().getId().equals(user.getId())) {
      throw new ForbiddenOperationException("자신이 생성한 부스의 타임슬롯만 삭제할 수 있습니다");
    }
    
    // 예약이 있는 경우 삭제 불가
    if (timeSlot.getCurrentCapacity() > 0) {
      throw new BadRequestException("예약이 존재하는 타임슬롯은 삭제할 수 없습니다");
    }
    
    timeSlotRepository.delete(timeSlot);
  }

  // 부스의 모든 TimeSlot 조회
  @Transactional(readOnly = true)
  public List<TimeSlotResponseDto> getTimeSlotsByBooth(Long boothId) {
    boothService.findBoothById(boothId); // 부스 존재 확인
    List<TimeSlot> timeSlots = timeSlotRepository.findByBoothId(boothId);
    return timeSlots.stream()
        .map(TimeSlotResponseDto::from)
        .collect(Collectors.toList());
  }

  // 부스의 예약 가능한 TimeSlot 조회
  @Transactional(readOnly = true)
  public List<TimeSlotResponseDto> getAvailableTimeSlots(Long boothId) {
    boothService.findBoothById(boothId); // 부스 존재 확인
    List<TimeSlot> timeSlots = timeSlotRepository.findAvailableTimeSlotsByBoothId(boothId);
    return timeSlots.stream()
        .map(TimeSlotResponseDto::from)
        .collect(Collectors.toList());
  }

  // TimeSlot 조회
  @Transactional(readOnly = true)
  public TimeSlot findTimeSlotById(Long id) {
    return timeSlotRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("TimeSlot not found: " + id));
  }

  @Transactional(readOnly = true)
  public TimeSlot findTimeSlotByIdAndBoothId(Long id, Long boothId) {
    return timeSlotRepository.findByIdAndBoothId(id, boothId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "TimeSlot not found with id " + id + " in booth " + boothId));
  }

  // 시간 유효성 검증
  private void validateTimeSlot(LocalDateTime startTime, LocalDateTime endTime, Booth booth) {
    if (startTime.isAfter(endTime)) {
      throw new BadRequestException("시작 시간은 종료 시간보다 앞서야 합니다");
    }
    
    if (startTime.isBefore(booth.getStartDateTime()) || endTime.isAfter(booth.getEndDateTime())) {
      throw new BadRequestException("타임슬롯은 부스 운영 시간 내에 있어야 합니다");
    }
  }
}
