package com.cenfotec.inkmapapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NarrativeVersionCompareResponseDTO {
    private Long versionAId;
    private Long versionBId;
    private String contentA;
    private String contentB;
    private LocalDateTime createdAtA;
    private LocalDateTime createdAtB;
}
