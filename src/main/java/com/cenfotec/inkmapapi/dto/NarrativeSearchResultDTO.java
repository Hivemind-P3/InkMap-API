package com.cenfotec.inkmapapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NarrativeSearchResultDTO {
    private Long narrativeId;
    private String narrativeTitle;
    private String snippet;
}
