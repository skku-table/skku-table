package com.skkutable.controller;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.dto.BoothCreateDto;
import com.skkutable.dto.BoothPatchDto;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.mapper.BoothMapper;
import com.skkutable.service.BoothService;
import com.skkutable.service.FestivalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/festivals/{festivalId}/booths")
class BoothController {

  private final BoothService boothService;
  private final FestivalService festivalService;
  private final BoothMapper boothMapper;

  BoothController(BoothService boothService, FestivalService festivalService,
      BoothMapper boothMapper) {
    this.boothService = boothService;
    this.festivalService = festivalService;
    this.boothMapper = boothMapper;
  }

  @PostMapping("/register")
  public Booth registerBooth(@PathVariable Long festivalId, @RequestBody @Valid BoothCreateDto dto) {
    Booth booth = boothMapper.toEntity(dto);   // Festival 없이 먼저 매핑
    return boothService.createBooth(festivalId, booth);
  }

  @PatchMapping("/{boothId}")
  public Booth patchBooth(@PathVariable Long festivalId, @PathVariable Long boothId, @RequestBody @Valid BoothPatchDto dto) {
    return boothService.patchUpdateBooth(festivalId, boothId, dto, festivalService);
  }

  @GetMapping("/{boothId}")
  public Booth getBoothByFestival(@PathVariable Long festivalId, @PathVariable Long boothId) {
    return boothService.findBoothByIdAndFestivalId(boothId, festivalId);
  }

}
