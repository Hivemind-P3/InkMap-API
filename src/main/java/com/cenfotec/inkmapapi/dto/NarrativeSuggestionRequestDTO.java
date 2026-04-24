package com.cenfotec.inkmapapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NarrativeSuggestionRequestDTO {
    private Long projectId;
    private String additionalInstructions;
}
