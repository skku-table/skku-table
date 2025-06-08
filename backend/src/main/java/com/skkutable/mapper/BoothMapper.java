package com.skkutable.mapper;

import com.skkutable.domain.Booth;
import com.skkutable.dto.BoothCreateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BoothMapper {

  Booth toEntity(BoothCreateDto dto);
}
