package com.cenfotec.inkmapapi.dto;

import com.cenfotec.inkmapapi.models.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StoryCharacterResponseDTO {

    private Long id;
    private String name;
    private String role;
    private String description;
    private Integer age;
    private Gender gender;
    private String race;
    private LocalDateTime createdAt;
}