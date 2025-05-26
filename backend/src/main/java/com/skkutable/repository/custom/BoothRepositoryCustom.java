package com.skkutable.repository.custom;

import com.skkutable.domain.Booth;

public interface BoothRepositoryCustom {
  /** festival-id를 기준으로 Booth를 저장하고 양방향 연관을 맞춰 준다. */
  Booth createBooth(Long festivalId, Booth booth);
}
