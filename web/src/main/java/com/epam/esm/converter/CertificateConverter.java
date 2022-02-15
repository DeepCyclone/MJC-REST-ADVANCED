package com.epam.esm.converter;

import com.epam.esm.dto.request.GiftCertificateDto;
import com.epam.esm.dto.response.GiftCertificateResponseDto;
import com.epam.esm.repository.model.GiftCertificate;
import com.epam.esm.service.impl.TagServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Component
public class CertificateConverter implements ConverterTemplate<GiftCertificate, GiftCertificateDto, GiftCertificateResponseDto> {


    private final TagServiceImpl tagServiceImpl;
    private final TagConverter tagConverter;
    @Autowired
    public CertificateConverter(TagServiceImpl tagServiceImpl, TagConverter tagConverter) {

        this.tagServiceImpl = tagServiceImpl;
        this.tagConverter = tagConverter;
    }

    @Override
    public GiftCertificate convertFromRequestDto(GiftCertificateDto dto) {
         return GiftCertificate.builder().
                id(dto.getId()).
                name(dto.getName()).
                description(dto.getDescription()).
                price(dto.getPrice()).
                duration(dto.getDuration()).
                associatedTags(tagConverter.convertFromRequestDtos(dto.getAssociatedTags())).
                build();
    }

    @Override
    public List<GiftCertificate> convertFromRequestDtos(List<GiftCertificateDto> dtos) {
        List<GiftCertificate> objects = new ArrayList<>();
        for(GiftCertificateDto gc:dtos){
            objects.add(convertFromRequestDto(gc));
        }
        return objects;
    }

    @Override
    public GiftCertificateResponseDto convertToResponseDto(GiftCertificate object) {
        return GiftCertificateResponseDto.builder().
                id(object.getId()).
                name(object.getName()).
                description(object.getDescription()).
                price(object.getPrice()).
                duration(object.getDuration()).
                createDate(new Date(object.getCreateDate().getTime())).
                lastUpdateDate(new Date(object.getLastUpdateDate().getTime())).
                associatedTags(tagConverter.convertToResponseDtos(object.getAssociatedTags())).
                build();
    }

    @Override
    public List<GiftCertificateResponseDto> convertToResponseDtos(List<GiftCertificate> objects) {
        List<GiftCertificateResponseDto> dtos = new ArrayList<>();
        for(GiftCertificate gc:objects){
            dtos.add(convertToResponseDto(gc));
        }
        return dtos;
    }
}
