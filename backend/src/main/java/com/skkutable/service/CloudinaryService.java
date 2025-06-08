package com.skkutable.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryService {

  private final Cloudinary cloudinary;

  public CloudinaryService(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }

  public String uploadImage(MultipartFile file) {
    try {
      Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
          ObjectUtils.asMap("resource_type", "auto"));
      return uploadResult.get("secure_url").toString();
    } catch (IOException e) {
      throw new RuntimeException("이미지 업로드 실패", e);
    }
  }

  public Map<String, String> uploadMultipleImages(Map<String, MultipartFile> files) {
    Map<String, String> result = new HashMap<>();
    for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
      String key = entry.getKey();
      MultipartFile file = entry.getValue();

      if (!file.isEmpty()) {
        try {
          Map uploadResult = cloudinary.uploader().upload(
              file.getBytes(),
              ObjectUtils.asMap("resource_type", "auto")
          );
          result.put(key, uploadResult.get("secure_url").toString());
        } catch (IOException e) {
          throw new RuntimeException("Cloudinary upload failed for: " + key, e);
        }
      }
    }
    return result;
  }
}
