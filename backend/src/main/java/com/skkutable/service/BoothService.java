package com.skkutable.service;

import com.skkutable.domain.Booth;
import com.skkutable.repository.BoothRepository;
import com.skkutable.domain.Festival;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BoothService {

  private final BoothRepository boothRepository;

  @Autowired
  public BoothService(BoothRepository boothRepository) {
    this.boothRepository = boothRepository;
  }

  public Booth createBooth(Booth booth) {
    // Festival과의 관계를 설정
    validateFestivalExists(booth.getFestival());
    return boothRepository.save(booth);
  }

  private void validateFestivalExists(Festival festival) {
    if (festival == null || festival.getId() == null) {
      throw new IllegalArgumentException("Festival must be provided");
    }
  }

  public List<Booth> findBoothsByFestival(Long festivalId) {
    return boothRepository.findByFestivalId(festivalId);
  }

  public Optional<Booth> findBoothById(Long boothId) {
    return boothRepository.findById(boothId);
  }

  public void deleteBooth(Long boothId) {
    boothRepository.deleteById(boothId);
  }

  public Optional<Booth> findBoothByIdAndFestivalId(Long boothId, Long festivalId) {
    return boothRepository.findByIdAndFestivalId(boothId, festivalId);
  }
  // 추가적인 비즈니스 로직이 필요하다면 여기에 추가할 수 있음
}