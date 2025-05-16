package com.skkutable.service;

import com.skkutable.domain.Festival;
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

  public Optional<Festival> findFestivalById(Long festivalId) {
    return festivalRepository.findById(festivalId);
  }

  public void deleteFestival(Long festivalId) {
    festivalRepository.deleteById(festivalId);
  }

  // 추가적인 비즈니스 로직이 필요하다면 여기에 추가할 수 있음
}