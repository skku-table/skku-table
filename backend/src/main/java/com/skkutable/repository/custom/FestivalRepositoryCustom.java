package com.skkutable.repository.custom;

import com.skkutable.domain.Festival;
import java.util.List;

public interface FestivalRepositoryCustom {

  /**
   * 이름·장소·설명 어디든 포함되면 매칭
   */
  List<Festival> searchDynamic(String keyword);
}
