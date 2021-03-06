package com.epam.esm.converter;

import com.epam.esm.dto.request.TagDto;
import com.epam.esm.repository.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagConverter {

    TagConverter INSTANCE = Mappers.getMapper(TagConverter.class);

    Tag convertFromRequestDto(TagDto dto);
    List<Tag> convertFromRequestDtos(List<TagDto> dtos);
}
