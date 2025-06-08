package com.skkutable.service;

import com.skkutable.domain.Festival;
import com.skkutable.dto.FestivalPatchDto;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.FestivalRepository;
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

  @Autowired
  public FestivalService(FestivalRepository festivalRepository, BoothService boothService) {
    this.festivalRepository = festivalRepository;
    this.boothService = boothService;
  }

  public List<Festival> search(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return festivalRepository.findAll();
    }
    return festivalRepository.searchDynamic(keyword);
  }

  public Festival createFestival(Festival festival) {
    // 중복된 이름 검증 등의 로직이 있다면 여기에 추가
    boolean exists = festivalRepository.existsByName(festival.getName());
    if (exists) {
      throw new IllegalArgumentException("이미 같은 이름의 축제가 존재합니다: " + festival.getName());
    }
    return festivalRepository.save(festival);
  }

  @Transactional
  public Festival updateImageUrls(Long id, Map<String, String> urls) {
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
    Festival festival = findFestivalById(id);
    festival.setPosterImageUrl(null);
    festival.setMapImageUrl(null);
  }

  public List<Festival> findFestivals() {
    return festivalRepository.findAll();
  }

  public Festival findFestivalById(Long festivalId) {
    return festivalRepository.findById(festivalId).
        orElseThrow(() -> new ResourceNotFoundException("Festival not found: " + festivalId));
  }

  public void deleteFestival(Long festivalId) {
    try {
      festivalRepository.deleteById(festivalId);
    } catch (EmptyResultDataAccessException e) {
      throw new ResourceNotFoundException("Festival not found: " + festivalId);
    }
  }

  public Festival patchUpdateFestival(Long id, FestivalPatchDto dto) {
    Festival fest = festivalRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Festival not found: " + id));

    fest.applyPatch(dto);
    return fest;
  }

  public Festival save(Festival festival) {
    return festivalRepository.save(festival);
  }
}