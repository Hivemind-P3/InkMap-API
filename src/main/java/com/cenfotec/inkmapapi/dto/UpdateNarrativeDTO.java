package com.cenfotec.inkmapapi.dto;

import lombok.*;

@Getter
@Setter
public class UpdateNarrativeDTO {
    private Long projectId;
    private String title;
    private String content;
}