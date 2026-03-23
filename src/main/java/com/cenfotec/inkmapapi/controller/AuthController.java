package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.*;
import com.cenfotec.inkmapapi.service.AuthService;
import com.cenfotec.inkmapapi.service.PasswordService;
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
    private final PasswordService passwordService;

    public AuthController(AuthService authService, PasswordService passwordService) {
        this.authService = authService;
        this.passwordService = passwordService;
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        passwordService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok("Correo enviado");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        passwordService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Contraseña actualizada");
    }
}