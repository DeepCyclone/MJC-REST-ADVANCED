package com.epam.esm.controller;

import com.epam.esm.converter.CertificateConverter;
import com.epam.esm.dto.CreateDTO;
import com.epam.esm.dto.PatchDTO;
import com.epam.esm.dto.request.GiftCertificateDto;
import com.epam.esm.dto.response.GiftCertificateResponseDto;
import com.epam.esm.repository.model.GiftCertificate;
import com.epam.esm.service.template.GiftCertificateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.constraints.Min;

@RestController
@RequestMapping(value = "/api/v1/certificates",produces = {MediaType.APPLICATION_JSON_VALUE})
public class GiftCertificateController {

    private final GiftCertificateService certificateService;
    private final CertificateConverter certificateConverter;

    @Autowired
    public GiftCertificateController(GiftCertificateService certificateService, CertificateConverter certificateConverter) {
        this.certificateService = certificateService;
        this.certificateConverter = certificateConverter;
    }

    @GetMapping
    public List<GiftCertificateResponseDto> getAllByRequestParams(@RequestParam MultiValueMap<String,String> params,
                                                                  @RequestParam(defaultValue = "1") @Min(1) long limit,
                                                                  @RequestParam(defaultValue = "0") @Min(0) long offset) {
        return certificateConverter.convertToResponseDtos(certificateService.handleParametrizedGetRequest(params,limit,offset));
    }

    @GetMapping(value = "/{id:\\d+}")
    public GiftCertificateResponseDto getByID(@PathVariable long id) {

        GiftCertificate giftCertificate = certificateService.getByID(id);
        return certificateConverter.convertToResponseDto(giftCertificate);
    }

    @DeleteMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificate(@PathVariable long id){
        certificateService.deleteByID(id);
    }

    @PatchMapping(value = "/{id:\\d+}")
    public ResponseEntity<GiftCertificateResponseDto> updateCertificate(@RequestBody @Validated(PatchDTO.class) GiftCertificateDto certificateDtoPatch,@PathVariable long id){
        GiftCertificate certificate = certificateConverter.convertFromRequestDto(certificateDtoPatch);
        GiftCertificateResponseDto a = certificateConverter.convertToResponseDto(certificateService.update(certificate,id));
        return new ResponseEntity<>(a,HttpStatus.CREATED);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public GiftCertificateResponseDto createCertificate(@RequestBody @Validated(CreateDTO.class) GiftCertificateDto certificateDto) {
        GiftCertificate certificate = certificateConverter.convertFromRequestDto(certificateDto);
        return certificateConverter.convertToResponseDto(certificateService.addEntity(certificate));
    }

}
