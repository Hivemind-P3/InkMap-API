package com.cenfotec.inkmapapi.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Servicio encargado de verificar el token recibido desde Google.
 */
@Service
public class GoogleTokenVerifierService {

    @Value("${google.client.id}")
    private String googleClientId;

    /**
     * Verifica un token de Google y retorna su payload.
     *
     * @param credential token recibido desde el frontend
     * @return payload del token válido
     * @throws GeneralSecurityException excepción de seguridad
     * @throws IOException excepción de entrada y salida
     */
    public GoogleIdToken.Payload verifyToken(String credential) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(credential);

        if (googleIdToken == null) {
            throw new RuntimeException("Token de Google inválido");
        }

        return googleIdToken.getPayload();
    }
}