package com.skkutable.controller;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.dto.FestivalCreateDto;
import com.skkutable.dto.FestivalPatchDto;
import com.skkutable.mapper.FestivalMapper;
import com.skkutable.service.BoothService;
import com.skkutable.service.CloudinaryService;
import com.skkutable.service.FestivalService;
import jakarta.validation.Valid;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/festivals")
class FestivalController {
  private final FestivalService festivalService;
  private final FestivalMapper festivalMapper;
  private final CloudinaryService cloudinaryService;

  @Autowired
  FestivalController(FestivalService festivalService, BoothService boothService,
                     FestivalMapper festivalMapper, CloudinaryService cloudinaryService) {
    this.festivalService = festivalService;
    this.festivalMapper = festivalMapper;
    this.cloudinaryService = cloudinaryService;
  }


  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Festival registerFestival(
          @RequestParam String name,
          @RequestParam(required = false) String description,
          @RequestParam(required = false) String location,
          @RequestParam Date startDate,
          @RequestParam Date endDate,
          @RequestParam MultipartFile posterImage,
          @RequestParam MultipartFile mapImage) {

    Map<String, String> urls = cloudinaryService.uploadMultipleImages(
            Map.of("posterImage", posterImage, "mapImage", mapImage));

    FestivalCreateDto dto = new FestivalCreateDto(name, description, location, startDate, endDate,
            urls.get("posterImage"), urls.get("mapImage"));

    return festivalService.createFestival(festivalMapper.toEntity(dto));
  }

  @PatchMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Festival updateFestivalImages(@PathVariable Long id,
                                       @RequestParam(required = false) MultipartFile posterImage,
                                       @RequestParam(required = false) MultipartFile mapImage) {
    Map<String, MultipartFile> imageMap = new HashMap<>();
    if (posterImage != null) imageMap.put("posterImage", posterImage);
    if (mapImage != null) imageMap.put("mapImage", mapImage);

    Map<String, String> urls = cloudinaryService.uploadMultipleImages(imageMap);
    return festivalService.updateImageUrls(id, urls);
  }

  @DeleteMapping("/{id}/images")
  public ResponseEntity<String> deleteFestivalImages(@PathVariable Long id) {
    festivalService.removeImageUrls(id);
    return ResponseEntity.ok("Festival image URLs removed");
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
