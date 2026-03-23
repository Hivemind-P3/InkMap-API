package com.cenfotec.inkmapapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdatePreferencesRequestDTO {
    private boolean notificacionesCorreo;
    private List<String> colores;
}
