package com.epam.esm.dto.request;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import javax.validation.Valid;
import javax.validation.constraints.Null;

import com.epam.esm.dto.CreateDTO;
import com.epam.esm.dto.PatchDTO;
import javax.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftCertificateDto implements Serializable {
    @NotNull(groups = PatchDTO.class)
    private long id;
    @NotNull(message = "Name cannot be empty")
    @Size(min = 5,max = 50,message = "name length constraints = [5,50]")
    private String name;
    @NotNull(message = "Description cannot be empty")
    @Size(min = 5,max = 50,message = "description length constraints = [5,50]")
    private String description;
    @NotNull
    private BigDecimal price;
    @NotNull
    private Integer duration;
    private List<@Valid TagDto> associatedTags;
}