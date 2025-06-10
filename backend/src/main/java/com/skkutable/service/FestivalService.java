package com.skkutable.service;

import com.skkutable.domain.Festival;
import com.skkutable.domain.Role;
import com.skkutable.domain.User;
import com.skkutable.dto.FestivalPatchDto;
import com.skkutable.exception.BadRequestException;
import com.skkutable.exception.ForbiddenOperationException;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.FestivalRepository;
import com.skkutable.repository.UserRepository;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FestivalService {

  private final FestivalRepository festivalRepository;
  private final BoothService boothService;
  private final UserRepository userRepository;

  @Autowired
  public FestivalService(FestivalRepository festivalRepository, BoothService boothService,
      UserRepository userRepository) {
    this.festivalRepository = festivalRepository;
    this.boothService = boothService;
    this.userRepository = userRepository;
  }

  public List<Festival> search(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return festivalRepository.findAll();
    }
    return festivalRepository.searchDynamic(keyword);
  }

  public Festival createFestival(Festival festival, String userEmail) {

    if (festival == null) {
      throw new BadRequestException("Festival data must be provided");
    }
    // ADMIN 권한 확인
    User creator = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

    if (creator.getRole() != Role.ADMIN) {
      throw new ForbiddenOperationException("Only administrators can create festivals");
    }

    // 중복된 이름 검증
    boolean exists = festivalRepository.existsByName(festival.getName());
    if (exists) {
      throw new IllegalArgumentException("이미 같은 이름의 축제가 존재합니다: " + festival.getName());
    }

    return festivalRepository.save(festival);
  }

  @Transactional
  public Festival updateImageUrls(Long id, Map<String, String> urls) {
    if (id == null) {
      throw new BadRequestException("Festival ID must be provided");
    }
    if (urls == null || urls.isEmpty()) {
      throw new BadRequestException("No image URLs provided");
    }

    Festival festival = findFestivalById(id);
    if (urls.containsKey("posterImage")) {
      festival.setPosterImageUrl(urls.get("posterImage"));
    }
    if (urls.containsKey("mapImage")) {
      festival.setMapImageUrl(urls.get("mapImage"));
    }
    return festival;
  }

  @Transactional
  public void removeImageUrls(Long id) {
    if (id == null) {
      throw new BadRequestException("Festival ID must be provided");
    }
    Festival festival = findFestivalById(id);
    festival.setPosterImageUrl(null);
    festival.setMapImageUrl(null);
  }

  public List<Festival> findFestivals() {
    return festivalRepository.findAll();
  }

  public Festival findFestivalById(Long festivalId) {
    if (festivalId == null) {
      throw new BadRequestException("Festival ID must be provided");
    }
    return festivalRepository.findById(festivalId).
        orElseThrow(() -> new ResourceNotFoundException("Festival not found: " + festivalId));
  }

  public void deleteFestival(Long festivalId) {
    if (festivalId == null) {
      throw new BadRequestException("Festival ID must be provided");
    }
    try {
      festivalRepository.deleteById(festivalId);
    } catch (EmptyResultDataAccessException e) {
      throw new ResourceNotFoundException("Festival not found: " + festivalId);
    }
  }

  public Festival patchUpdateFestival(Long id, FestivalPatchDto dto) {
    if (id == null) {
      throw new BadRequestException("Festival ID must be provided");
    }
    if (dto == null) {
      throw new BadRequestException("Festival patch data must be provided");
    }
    Festival fest = festivalRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Festival not found: " + id));

    fest.applyPatch(dto);
    return fest;
  }

  public Festival save(Festival festival) {
    return festivalRepository.save(festival);
  }
}