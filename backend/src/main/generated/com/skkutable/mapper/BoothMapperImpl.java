package com.skkutable.mapper;

import com.skkutable.domain.Booth;
import com.skkutable.dto.BoothCreateDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-25T01:53:01+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class BoothMapperImpl implements BoothMapper {

    @Override
    public Booth toEntity(BoothCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Booth.BoothBuilder booth = Booth.builder();

        booth.name( dto.getName() );
        booth.host( dto.getHost() );
        booth.location( dto.getLocation() );
        booth.description( dto.getDescription() );
        booth.startDateTime( dto.getStartDateTime() );
        booth.endDateTime( dto.getEndDateTime() );
        booth.posterImageUrl( dto.getPosterImageUrl() );
        booth.eventImageUrl( dto.getEventImageUrl() );

        return booth.build();
    }
}
