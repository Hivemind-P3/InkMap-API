package com.cenfotec.inkmapapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WikiResponseDTO {

    private Long id;
    private String title;
    private String content;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
