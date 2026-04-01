package com.cenfotec.inkmapapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String medium;
    private List<String> tags;
    private LocalDateTime createdAt;
}
