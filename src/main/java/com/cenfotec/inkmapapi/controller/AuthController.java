package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.GoogleAuthRequestDTO;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Controlador encargado de los endpoints de autenticación.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Permite iniciar sesión con Google.
     *
     * @param request solicitud con el token de Google
     * @return usuario autenticado
     * @throws GeneralSecurityException excepción de seguridad
     * @throws IOException excepción de entrada y salida
     */
    @PostMapping("/google")
    public ResponseEntity<User> loginWithGoogle(@RequestBody GoogleAuthRequestDTO request)
            throws GeneralSecurityException, IOException {
        return ResponseEntity.ok(authService.loginWithGoogle(request.getCredential()));
    }
}