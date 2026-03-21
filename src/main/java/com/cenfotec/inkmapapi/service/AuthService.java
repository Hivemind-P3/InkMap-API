package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.AuthResponseDTO;
import com.cenfotec.inkmapapi.dto.LoginRequestDTO;
import com.cenfotec.inkmapapi.dto.RegisterRequestDTO;
import com.cenfotec.inkmapapi.dto.UserResponseDTO;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Servicio encargado de la lógica de autenticación.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       GoogleTokenVerifierService googleTokenVerifierService,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registra un nuevo usuario local con email y contraseña.
     *
     * @param request datos de registro
     * @return respuesta de autenticación con token y datos del usuario
     */
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider("LOCAL");
        user.setRole("USUARIO");

        user = userRepository.save(user);
        return buildAuthResponse(user);
    }

    /**
     * Autentica un usuario local con email y contraseña.
     *
     * @param request credenciales de login
     * @return respuesta de autenticación con token y datos del usuario
     */
    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    /**
     * Autentica o registra un usuario mediante token de Google.
     *
     * @param credential token de Google recibido desde el frontend
     * @return respuesta de autenticación con token y datos del usuario
     * @throws GeneralSecurityException excepción de seguridad
     * @throws IOException excepción de entrada y salida
     */
    public AuthResponseDTO loginWithGoogle(String credential) throws GeneralSecurityException, IOException {
        GoogleIdToken.Payload payload = googleTokenVerifierService.verifyToken(credential);

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setProvider("GOOGLE");
            newUser.setRole("USUARIO");
            return userRepository.save(newUser);
        });

        return buildAuthResponse(user);
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        UserResponseDTO userDto = new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getProvider(), user.getRole());
        return new AuthResponseDTO(token, userDto);
    }
}
