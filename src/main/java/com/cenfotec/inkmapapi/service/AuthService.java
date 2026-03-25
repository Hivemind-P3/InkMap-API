package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.AuthResponseDTO;
import com.cenfotec.inkmapapi.dto.LoginRequestDTO;
import com.cenfotec.inkmapapi.dto.RegisterRequestDTO;
import com.cenfotec.inkmapapi.dto.UserResponseDTO;
import com.cenfotec.inkmapapi.models.ColorCode;
import com.cenfotec.inkmapapi.models.Preferences;
import com.cenfotec.inkmapapi.models.Role;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.ColorCodeRepository;
import com.cenfotec.inkmapapi.repository.PreferencesRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import com.cenfotec.inkmapapi.util.PasswordValidator;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Servicio encargado de la lógica de autenticación.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PreferencesRepository preferencesRepository;
    private final ColorCodeRepository colorCodeRepository;
    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PreferencesService preferencesService;

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

        PasswordValidator.validate(request.getPassword());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider("LOCAL");
        user.setRole(Role.USER);
        user = userRepository.save(user);

        preferencesService.setDefaultPreferences(user);

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
            newUser.setRole(Role.USER);
          
            preferencesService.setDefaultPreferences(newUser);
            return userRepository.save(newUser);
        });

        return buildAuthResponse(user);
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
      
        Preferences preferences = preferencesRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preferences not found for user " + user.getId()));
      
        UserResponseDTO userDto = new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getProvider(), user.getRole().name(), user.getStartDt(), preferences);
      
        return new AuthResponseDTO(token, userDto);
    }
}