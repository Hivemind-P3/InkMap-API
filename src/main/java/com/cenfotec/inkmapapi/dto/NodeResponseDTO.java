package com.cenfotec.inkmapapi.dto;

import com.cenfotec.inkmapapi.models.enums.NodeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NodeResponseDTO {

    private Long id;
    private String label;
    private String description;
    private NodeType type;
    private String color;
    private Double posX;
    private Double posY;
    private Long nodeMapId;
}