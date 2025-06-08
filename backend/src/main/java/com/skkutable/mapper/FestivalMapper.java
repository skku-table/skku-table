package com.skkutable.mapper;

import com.skkutable.domain.Festival;
import com.skkutable.dto.FestivalCreateDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface FestivalMapper {

  Festival toEntity(FestivalCreateDto dto);
}