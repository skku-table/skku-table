package com.skkutable.service;

import com.skkutable.domain.Booth;
import com.skkutable.dto.BoothPatchDto;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.BoothRepository;
import com.skkutable.domain.Festival;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.nio.file.ReadOnlyFileSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BoothService {

  @PersistenceContext
  private EntityManager em;

  private final BoothRepository boothRepository;

  @Autowired
  public BoothService(BoothRepository boothRepository) {
    this.boothRepository = boothRepository;
  }

  public Booth createBooth(Long festivalId, Booth booth) {
    // ① 영속성 컨텍스트 안에서 proxy reference 획득
    Festival festivalRef = em.getReference(Festival.class, festivalId);
    if (festivalRef == null) throw new ResourceNotFoundException("Festival not found: " + festivalId);

    // ② 양방향 동기화
    festivalRef.addBooth(booth);   // festival.booths 에도 추가
    // 또는 booth.setFestival(festivalRef);

    // ③ 저장 – festivalRef 는 managed 상태
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

  public Booth findBoothById(Long boothId) {

    return boothRepository.findById(boothId).
        orElseThrow((
            () -> new ResourceNotFoundException("Booth not found: " + boothId)
            ));
  }

  public void deleteBooth(Long boothId) {
    boothRepository.deleteById(boothId);
  }

  public Booth findBoothByIdAndFestivalId(Long boothId, Long festivalId) {
    return boothRepository.findByIdAndFestivalId(boothId, festivalId).
        orElseThrow(() -> new ResourceNotFoundException("Booth not found: " + boothId));
  }

  public Booth patchUpdateBooth(Long festivalId, Long boothId, BoothPatchDto dto, FestivalService festivalService) {

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