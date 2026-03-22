package com.cenfotec.inkmapapi.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO que representa la solicitud de autenticación con Google.
 */
@Getter
@Setter
public class GoogleAuthRequestDTO {

    private String credential;
}