package com.cenfotec.inkmapapi.dto;

import com.cenfotec.inkmapapi.models.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStoryCharacterRequestDTO {

    private String name;
    private String role;
    private String description;
    private Integer age;
    private Gender gender;
    private String race;
}