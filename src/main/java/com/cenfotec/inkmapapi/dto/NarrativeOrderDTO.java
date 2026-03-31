package com.cenfotec.inkmapapi.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class NarrativeOrderDTO {
    private Long projectId;
    private List<Long> orderedIds;
}