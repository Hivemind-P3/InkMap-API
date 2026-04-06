package com.cenfotec.inkmapapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNodeRequestDTO {

    private String label;
    private String description;
    private Double posX;
    private Double posY;
}