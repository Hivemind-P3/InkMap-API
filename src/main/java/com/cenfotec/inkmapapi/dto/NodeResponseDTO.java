package com.cenfotec.inkmapapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NodeResponseDTO {

    private Long id;
    private String label;
    private String description;
    private Double posX;
    private Double posY;
    private Long nodeMapId;
}