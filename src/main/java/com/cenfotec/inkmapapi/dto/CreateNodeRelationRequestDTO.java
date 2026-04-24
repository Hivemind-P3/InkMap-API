package com.cenfotec.inkmapapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNodeRelationRequestDTO {

    private Long sourceNodeId;
    private Long targetNodeId;
}
