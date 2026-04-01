package com.cenfotec.inkmapapi.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PasswordValidator {

    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$";

    private static final String ERROR_MESSAGE =
            "La contraseña debe tener al menos 8 caracteres, una letra mayúscula y un carácter especial";

    public static void validate(String password) {
        if (password == null || !password.matches(PASSWORD_REGEX)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
        }
    }
}
