package com.skkutable.controller;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.dto.FestivalCreateDto;
import com.skkutable.dto.FestivalPatchDto;
import com.skkutable.mapper.FestivalMapper;
import com.skkutable.service.BoothService;
import com.skkutable.service.FestivalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/festivals")
class FestivalController {
  private final FestivalService festivalService;
  private final FestivalMapper festivalMapper;

  @Autowired
  FestivalController(FestivalService festivalService, BoothService boothService,
      FestivalMapper festivalMapper) {
    this.festivalService = festivalService;
    this.festivalMapper = festivalMapper;
  }


  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public Festival registerFestival(@RequestBody @Valid FestivalCreateDto festivalCreateDto) {
    return festivalService.createFestival(festivalMapper.toEntity(festivalCreateDto));
  }

  @GetMapping("{id}")
  public Festival getFestival(@PathVariable Long id) {
    return festivalService.findFestivalById(id);
  }

  @PatchMapping("/{id}")
  public Festival patchFestival(@PathVariable Long id, @RequestBody @Valid FestivalPatchDto dto) {
    return festivalService.patchUpdateFestival(id, dto);
  }

  /**
   * GET /festivals?q=키워드
   *   - q 파라미터 없으면 전체 목록
   *   - q 파라미터 있으면 이름·장소·설명에서 부분 일치 검색
   */
  @GetMapping
  public List<Festival> getFestivals(@RequestParam(value = "q", required = false) String q) {
    return festivalService.search(q);
  }
}
