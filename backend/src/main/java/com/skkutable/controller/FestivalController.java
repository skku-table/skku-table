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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class FestivalController {
  private final FestivalService festivalService;
  private final BoothService boothService;
  private final FestivalMapper festivalMapper;

  @Autowired
  FestivalController(FestivalService festivalService, BoothService boothService,
      FestivalMapper festivalMapper) {
    this.festivalService = festivalService;
    this.boothService = boothService;
    this.festivalMapper = festivalMapper;
  }

  @GetMapping("/festivals")
  public List<Festival> getFestivals() {
    return festivalService.findFestivals();
  }

  @PostMapping("/festivals/register")
  public Festival registerFestival(@RequestBody @Valid FestivalCreateDto festivalCreateDto) {
    return festivalService.createFestival(festivalMapper.toEntity(festivalCreateDto));
  }

  @GetMapping("/festivals/{id}")
  public Festival getFestival(@PathVariable Long id) {
    return festivalService.findFestivalById(id)
        .orElseThrow((() -> new IllegalArgumentException("Festival not found with id: " + id)));
  }

  @PatchMapping("/festivals/{id}")
  public Festival patchFestival(@PathVariable Long id, @RequestBody @Valid FestivalPatchDto dto) {
    return festivalService.patchUpdateFestival(id, dto);
  }
}
