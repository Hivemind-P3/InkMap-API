package com.cenfotec.inkmapapi.dto;

import com.cenfotec.inkmapapi.models.Preferences;
import com.cenfotec.inkmapapi.models.enums.Role;
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
    private String role;
    private LocalDateTime startDt;
    private Preferences preferences;
}