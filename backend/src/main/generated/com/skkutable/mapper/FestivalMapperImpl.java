package com.skkutable.mapper;

import com.skkutable.domain.Festival;
import com.skkutable.dto.FestivalCreateDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-25T01:53:01+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class FestivalMapperImpl implements FestivalMapper {

    @Override
    public Festival toEntity(FestivalCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Festival.FestivalBuilder festival = Festival.builder();

        festival.posterImageUrl( dto.getPosterImageUrl() );
        festival.mapImageUrl( dto.getMapImageUrl() );
        festival.name( dto.getName() );
        festival.startDate( dto.getStartDate() );
        festival.endDate( dto.getEndDate() );
        festival.location( dto.getLocation() );
        festival.description( dto.getDescription() );

        return festival.build();
    }
}
