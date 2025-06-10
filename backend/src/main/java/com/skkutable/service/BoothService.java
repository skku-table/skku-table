package com.skkutable.service;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.domain.User;
import com.skkutable.dto.BoothPatchDto;
import com.skkutable.exception.BadRequestException;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.BoothRepository;
import com.skkutable.repository.UserRepository;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BoothService {

  private final BoothRepository boothRepository;
  private final UserRepository userRepository;

  @Autowired
  public BoothService(BoothRepository boothRepository, UserRepository userRepository) {
    this.boothRepository = boothRepository;
    this.userRepository = userRepository;
  }

  public Booth createBooth(Long festivalId, Booth booth, String userEmail) {
    if (festivalId == null || booth == null) {
      throw new BadRequestException("Festival ID and Booth data must be provided");
    }

    // 생성자 정보 설정
    User creator = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
    booth.setCreatedBy(creator);

    return boothRepository.createBooth(festivalId, booth);
  }

  @Transactional
  public Booth updateImageUrls(Long festivalId, Long boothId, Map<String, String> urls) {
    if (festivalId == null || boothId == null) {
      throw new BadRequestException("Festival ID and Booth ID must be provided");
    }

    if (urls == null || urls.isEmpty()) {
      throw new BadRequestException("No image URLs provided");
    }

    Booth booth = findBoothByIdAndFestivalId(boothId, festivalId);
    if (urls.containsKey("posterImage")) {
      booth.setPosterImageUrl(urls.get("posterImage"));
    }
    if (urls.containsKey("eventImage")) {
      booth.setEventImageUrl(urls.get("eventImage"));
    }
    return booth;
  }

  @Transactional
  public void removeImageUrls(Long festivalId, Long boothId) {
    if (festivalId == null || boothId == null) {
      throw new BadRequestException("Festival ID and Booth ID must be provided");
    }
    Booth booth = findBoothByIdAndFestivalId(boothId, festivalId);
    booth.setPosterImageUrl(null);
    booth.setEventImageUrl(null);
  }

  private void validateFestivalExists(Festival festival) {
    if (festival == null || festival.getId() == null) {
      throw new BadRequestException("Festival must be provided");
    }
  }

  public List<Booth> findBoothsByFestival(Long festivalId) {
    if (festivalId == null) {
      throw new BadRequestException("Festival ID must be provided");
    }
    return boothRepository.findByFestivalId(festivalId);
  }

  public Booth findBoothById(Long boothId) {
    if (boothId == null) {
      throw new BadRequestException("Booth ID must be provided");
    }
    return boothRepository.findById(boothId).
        orElseThrow((
            () -> new ResourceNotFoundException("Booth not found: " + boothId)
        ));
  }

  public Booth save(Booth booth) {
    if (booth == null) {
      throw new BadRequestException("Booth data must be provided");
    }
    return boothRepository.save(booth);
  }

  public void deleteBooth(Long boothId) {
    if (boothId == null) {
      throw new BadRequestException("Booth ID must be provided");
    }
    try {
      boothRepository.deleteById(boothId);
    } catch (EmptyResultDataAccessException e) {
      throw new ResourceNotFoundException("Booth not found: " + boothId);
    }
  }

  public Booth findBoothByIdAndFestivalId(Long boothId, Long festivalId) {
    if (boothId == null || festivalId == null) {
      throw new BadRequestException("Booth ID and Festival ID must be provided");
    }
    return boothRepository.findByIdAndFestivalId(boothId, festivalId).
        orElseThrow(() -> new ResourceNotFoundException(
            "Booth not found with id " + boothId + " in festival " + festivalId));
  }

  public Booth patchUpdateBooth(Long festivalId, Long boothId, BoothPatchDto dto,
      FestivalService festivalService) {

    if (festivalId == null || boothId == null) {
      throw new BadRequestException("Festival ID and Booth ID must be provided");
    }

    festivalService.findFestivalById(festivalId);
    Booth booth = boothRepository.findById(boothId)
        .orElseThrow(() -> new ResourceNotFoundException("Booth not found: " + boothId));

    Festival targetFestival = null;
    if (dto.getFestivalId() != null &&              // festivalId가 요청에 포함됐고
        !dto.getFestivalId().equals(booth.getFestival().getId())) {  // 현재와 다르면
      targetFestival = festivalService.findFestivalById(dto.getFestivalId());
    }

    booth.applyPatch(dto, targetFestival);   // Dirty Checking
    return booth;                            // flush 시점에 UPDATE
  }
}