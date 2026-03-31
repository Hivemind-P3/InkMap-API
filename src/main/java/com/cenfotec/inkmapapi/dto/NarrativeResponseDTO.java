package com.cenfotec.inkmapapi.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class NarrativeResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Integer order;
}