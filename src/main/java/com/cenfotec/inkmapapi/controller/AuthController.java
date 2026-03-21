package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.AuthResponseDTO;
import com.cenfotec.inkmapapi.dto.GoogleAuthRequestDTO;
import com.cenfotec.inkmapapi.dto.LoginRequestDTO;
import com.cenfotec.inkmapapi.dto.RegisterRequestDTO;
import com.cenfotec.inkmapapi.service.AuthService;
import org.springframework.http.HttpStatus;
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
     * Registra un nuevo usuario con email y contraseña.
     *
     * @param request datos de registro (name, email, password)
     * @return token JWT y datos del usuario creado
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * Autentica un usuario local con email y contraseña.
     *
     * @param request credenciales (email, password)
     * @return token JWT y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Autentica o registra un usuario mediante Google OAuth.
     *
     * @param request solicitud con el token de Google (credential)
     * @return token JWT y datos del usuario
     * @throws GeneralSecurityException excepción de seguridad
     * @throws IOException excepción de entrada y salida
     */
    @PostMapping("/google")
    public ResponseEntity<AuthResponseDTO> loginWithGoogle(@RequestBody GoogleAuthRequestDTO request)
            throws GeneralSecurityException, IOException {
        return ResponseEntity.ok(authService.loginWithGoogle(request.getCredential()));
    }
}