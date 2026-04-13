package com.cenfotec.inkmapapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GeographicMapDTO {
    private Long id;
    private String name;
    private String konvaJson;
    private String thumbnail;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
