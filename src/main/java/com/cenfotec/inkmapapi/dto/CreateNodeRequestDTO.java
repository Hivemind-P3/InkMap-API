package com.cenfotec.inkmapapi.dto;

import com.cenfotec.inkmapapi.models.enums.NodeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNodeRequestDTO {

    private String label;
    private String description;
    private NodeType type;
    private String color;
    private Double posX;
    private Double posY;
}