package com.skkutable.controller;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.dto.FestivalDto;
import com.skkutable.service.BoothService;
import com.skkutable.service.FestivalService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class FestivalController {
  private final FestivalService festivalService;
  private final BoothService boothService;

  @Autowired
  public FestivalController(FestivalService festivalService, BoothService boothService) {
    this.festivalService = festivalService;
    this.boothService = boothService;
  }

  @GetMapping("/festivals")
  @ResponseBody
  public List<Festival> getFestivals() {
    return festivalService.findFestivals();
  }

  @PostMapping("/festivals/register")
  @ResponseBody
  public Festival registerFestival(@RequestBody FestivalDto festivalDto) {
    Festival festival = new Festival(festivalDto.getPosterImageUrl(), festivalDto.getMapImageUrl(),
        festivalDto.getName(), festivalDto.getStartDate(), festivalDto.getEndDate(), festivalDto.getLocation(), festivalDto.getDescription());
    festivalService.createFestival(festival);
    return festival;
  }

  @GetMapping("/festivals/{id}")
  @ResponseBody
  public Festival getFestival(@PathVariable Long id) {
    return festivalService.findFestivalById(id)
        .orElseThrow((() -> new IllegalArgumentException("Festival not found with id: " + id)));
  }
  @GetMapping("/festivals/{festivalId}/booth/{boothId}")
  @ResponseBody
  public Booth getBoothByFestival(@PathVariable Long festivalId, @PathVariable Long boothId) {
    Festival festival = festivalService.findFestivalById(festivalId)
        .orElseThrow(() -> new IllegalArgumentException("Festival not found with id: " + festivalId));

    return boothService.findBoothByIdAndFestivalId(boothId, festivalId)
        .orElseThrow(() -> new IllegalArgumentException("Booth not found with id: " + boothId));
  }

}
