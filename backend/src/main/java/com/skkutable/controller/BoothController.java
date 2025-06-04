package com.skkutable.controller;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.dto.BoothCreateDto;
import com.skkutable.dto.BoothPatchDto;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.mapper.BoothMapper;
import com.skkutable.service.BoothService;
import com.skkutable.service.CloudinaryService;
import com.skkutable.service.FestivalService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/festivals/{festivalId}/booths")
class BoothController {

  private final BoothService boothService;
  private final FestivalService festivalService;
  private final BoothMapper boothMapper;
  private final CloudinaryService cloudinaryService;

  BoothController(BoothService boothService, FestivalService festivalService,
                  BoothMapper boothMapper, CloudinaryService cloudinaryService) {
    this.boothService = boothService;
    this.festivalService = festivalService;
    this.boothMapper = boothMapper;
    this.cloudinaryService = cloudinaryService;
  }

  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Booth registerBooth(
          @PathVariable Long festivalId,
          @RequestParam String name,
          @RequestParam String host,
          @RequestParam(required = false) String location,
          @RequestParam(required = false) String description,
          @RequestParam LocalDateTime startDateTime,
          @RequestParam LocalDateTime endDateTime,
          @RequestParam MultipartFile posterImage,
          @RequestParam MultipartFile eventImage
  ) {
    Map<String, String> urls = cloudinaryService.uploadMultipleImages(
            Map.of("posterImage", posterImage, "eventImage", eventImage)
    );

    BoothCreateDto dto = new BoothCreateDto();
    dto.setFestivalId(festivalId);
    dto.setName(name);
    dto.setHost(host);
    dto.setLocation(location);
    dto.setDescription(description);
    dto.setStartDateTime(startDateTime);
    dto.setEndDateTime(endDateTime);
    dto.setPosterImageUrl(urls.get("posterImage"));
    dto.setEventImageUrl(urls.get("eventImage"));

    Booth booth = boothMapper.toEntity(dto);
    return boothService.createBooth(festivalId, booth);
  }

  @PatchMapping(value = "/{boothId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Booth updateBoothImages(@PathVariable Long festivalId,
                                 @PathVariable Long boothId,
                                 @RequestParam(required = false) MultipartFile posterImage,
                                 @RequestParam(required = false) MultipartFile eventImage) {
    Map<String, MultipartFile> imageMap = new HashMap<>();
    if (posterImage != null) imageMap.put("posterImage", posterImage);
    if (eventImage != null) imageMap.put("eventImage", eventImage);

    Map<String, String> urls = cloudinaryService.uploadMultipleImages(imageMap);
    return boothService.updateImageUrls(festivalId, boothId, urls);
  }

  @DeleteMapping("/{boothId}/images")
  public ResponseEntity<String> deleteBoothImages(@PathVariable Long festivalId,
                                                  @PathVariable Long boothId) {
    boothService.removeImageUrls(festivalId, boothId);
    return ResponseEntity.ok("Booth image URLs removed");
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
