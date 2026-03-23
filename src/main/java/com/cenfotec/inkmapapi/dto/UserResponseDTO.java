package com.cenfotec.inkmapapi.dto;

import com.cenfotec.inkmapapi.models.ColorCode;
import com.cenfotec.inkmapapi.models.Preferences;
import com.cenfotec.inkmapapi.models.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String provider;
    private RoleEnum role;
    private LocalDateTime startDt;
    private Preferences preferences;
}