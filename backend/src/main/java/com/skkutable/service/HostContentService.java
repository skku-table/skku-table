package com.skkutable.service;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Role;
import com.skkutable.domain.User;
import com.skkutable.dto.HostContentResponseDto;
import com.skkutable.dto.HostContentResponseDto.BoothResponse;
import com.skkutable.dto.HostContentResponseDto.FestivalWithBooths;
import com.skkutable.exception.ForbiddenOperationException;
import com.skkutable.repository.BoothRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HostContentService {

  private final UserService userService;
  private final BoothRepository boothRepository;

  public HostContentResponseDto getHostContent(String userEmail) {
    User user = userService.getCurrentUser(userEmail);

    // HOST 권한 확인
    if (user.getRole() != Role.HOST && user.getRole() != Role.ADMIN) {
      throw new ForbiddenOperationException("Only hosts can access this resource");
    }

    // 호스트가 생성한 모든 부스 조회
    List<Booth> userBooths = boothRepository.findByCreatedById(user.getId());

    // 축제별로 부스 그룹화
    Map<Long, List<Booth>> boothsByFestival = userBooths.stream()
        .collect(Collectors.groupingBy(booth -> booth.getFestival().getId()));

    // 축제와 해당 부스들을 매핑
    List<FestivalWithBooths> festivalsWithBooths = boothsByFestival.entrySet().stream()
        .map(entry -> {
          List<Booth> festivalBooths = entry.getValue();
          if (!festivalBooths.isEmpty()) {
            // 첫 번째 부스에서 축제 정보를 가져옴
            return FestivalWithBooths.from(festivalBooths.get(0).getFestival(), festivalBooths);
          }
          return null;
        })
        .filter(item -> item != null)
        .toList();

    return HostContentResponseDto.builder()
        .festivals(festivalsWithBooths)
        .booths(new ArrayList<>()) // 모든 부스는 축제에 속하므로 빈 리스트
        .build();
  }

  // 특정 축제의 호스트가 생성한 부스만 조회
  public List<BoothResponse> getHostBoothsByFestival(String userEmail, Long festivalId) {
    User user = userService.getCurrentUser(userEmail);

    if (user.getRole() != Role.HOST && user.getRole() != Role.ADMIN) {
      throw new ForbiddenOperationException("Only hosts can access this resource");
    }

    List<Booth> booths = boothRepository.findByFestivalIdAndCreatedById(festivalId, user.getId());

    return booths.stream()
        .map(BoothResponse::from)
        .toList();
  }
}
