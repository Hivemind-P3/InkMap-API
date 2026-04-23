package com.cenfotec.inkmapapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NarrativeAssociationsRequestDTO {
    private Long projectId;
    private List<Long> characterIds;
    private List<Long> wikiIds;
}