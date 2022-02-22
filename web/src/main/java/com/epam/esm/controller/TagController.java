package com.epam.esm.controller;

import com.epam.esm.converter.TagConverter;
import com.epam.esm.dto.CreateDTO;
import com.epam.esm.dto.request.TagDto;
import com.epam.esm.dto.response.TagResponseDto;
import com.epam.esm.repository.model.Tag;
import com.epam.esm.service.impl.TagServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/tags",produces = {MediaType.APPLICATION_JSON_VALUE})
public class TagController {


    private final TagServiceImpl tagServiceImpl;
    private final TagConverter tagConverter;

    @Autowired
    public TagController(TagServiceImpl tagServiceImpl, TagConverter tagConverter) {
        this.tagServiceImpl = tagServiceImpl;
        this.tagConverter = tagConverter;
    }

    @GetMapping(value = "/{id:\\d+}")
    public TagResponseDto getByID(@PathVariable("id") Long id){
        return tagConverter.convertToResponseDto(tagServiceImpl.getByID(id));
    }

    @GetMapping()
    public List<TagResponseDto> getTags(){
        return tagConverter.convertToResponseDtos(tagServiceImpl.getAll());
    }

    @DeleteMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable("id") Long id){
        tagServiceImpl.deleteByID(id);
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponseDto createTag(@Validated(CreateDTO.class) @RequestBody TagDto tagDto){
        Tag tag = tagConverter.convertFromRequestDto(tagDto);
        return tagConverter.convertToResponseDto(tagServiceImpl.addEntity(tag));
    }

}
