package com.skkutable.service;

import com.skkutable.domain.Festival;
import com.skkutable.dto.FestivalPatchDto;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.FestivalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

  public Festival createFestival(Festival festival) {
    // 중복된 이름 검증 등의 로직이 있다면 여기에 추가
    return festivalRepository.save(festival);
  }

  public List<Festival> findFestivals() {
    return festivalRepository.findAll();
  }

  public Festival findFestivalById(Long festivalId) {
    return festivalRepository.findById(festivalId).
        orElseThrow(() -> new ResourceNotFoundException("Festival not found: " + festivalId));
  }

  public void deleteFestival(Long festivalId) {
    festivalRepository.deleteById(festivalId);
  }

  public Festival patchUpdateFestival(Long id, FestivalPatchDto dto) {
    Festival fest = festivalRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Festival not found: " + id));

    fest.applyPatch(dto);
    return fest;
  }
}