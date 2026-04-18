package com.cenfotec.inkmapapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PointOfInterestResponseDTO {
    private Long id;
    private Double posX;
    private Double posY;
    private Long geographicMapId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
