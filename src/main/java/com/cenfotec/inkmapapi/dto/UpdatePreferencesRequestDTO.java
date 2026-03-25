package com.cenfotec.inkmapapi.dto;

import com.cenfotec.inkmapapi.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdatePreferencesRequestDTO {
    private User user;
    private boolean notificacionesCorreo;
    private List<String> colores;
}
