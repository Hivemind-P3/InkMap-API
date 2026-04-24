package com.cenfotec.inkmapapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NodeRelationResponseDTO {

    private Long id;
    private Long sourceNodeId;
    private Long targetNodeId;
    private Long nodeMapId;
}
