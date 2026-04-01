package com.cenfotec.inkmapapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateProjectRequestDTO {

    private String title;
    private String description;
    private String medium;
    private List<String> tags;
}
