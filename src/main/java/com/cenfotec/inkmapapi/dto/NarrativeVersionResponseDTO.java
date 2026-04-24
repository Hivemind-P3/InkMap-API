package com.cenfotec.inkmapapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class NarrativeVersionResponseDTO {
    private Long id;
    private Long narrativeId;
    private String content;
    private LocalDateTime createdAt;
    private String author;
}