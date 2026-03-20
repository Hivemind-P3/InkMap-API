package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

/**
 * Servicio encargado de la lógica de autenticación.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final GoogleTokenVerifierService googleTokenVerifierService;

    public AuthService(UserRepository userRepository, GoogleTokenVerifierService googleTokenVerifierService) {
        this.userRepository = userRepository;
        this.googleTokenVerifierService = googleTokenVerifierService;
    }

    /**
     * Inicia sesión con Google. Si el usuario no existe, lo crea.
     *
     * @param credential token de Google recibido desde el frontend
     * @return usuario existente o nuevo usuario registrado
     * @throws GeneralSecurityException excepción de seguridad
     * @throws IOException excepción de entrada y salida
     */
    public User loginWithGoogle(String credential) throws GeneralSecurityException, IOException {
        GoogleIdToken.Payload payload = googleTokenVerifierService.verifyToken(credential);

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setProvider("GOOGLE");

        return userRepository.save(user);
    }
}