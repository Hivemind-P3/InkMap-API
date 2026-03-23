package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.UpdatePreferencesRequestDTO;
import com.cenfotec.inkmapapi.models.ColorCode;
import com.cenfotec.inkmapapi.models.Preferences;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.ColorCodeRepository;
import com.cenfotec.inkmapapi.repository.PreferencesRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Servicio encargado de manejar la lógica de preferencias
 */
@Service
@RequiredArgsConstructor
public class PreferencesService {

    private final PreferencesRepository preferencesRepository;
    private final ColorCodeRepository colorCodeRepository;

    /**
     * Actualiza las preferencias del usuairo
     *
     * @param id ID del usuario que se quiere actualizar
     * @param dto Body del request
     * @return objeto preferences
     */
    public ResponseEntity<?> updatePreferences(Long id, UpdatePreferencesRequestDTO dto) {
        Preferences preferences = preferencesRepository.findByUserId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preferences not found for user " + id));

        preferences.setNotificacionesCorreo(dto.isNotificacionesCorreo());

        ColorCode colorCode = preferences.getColorCode();
        colorCode.setColores(dto.getColores());
        colorCodeRepository.save(colorCode);

        preferencesRepository.save(preferences);
        return ResponseEntity.ok(preferences);
    }

    /**
     * Crea las preferencias por defecto del usuario especificado
     *
     * @param user usuario que se usará de base para crear las preferencias
     */
    public void setDefaultPreferences(User user) {
        ColorCode colorCode = new ColorCode();
        colorCode.setColores(List.of("#D5637D", "#DF9781", "#E2C683", "#2BB08D", "#2A7C98", "#123540"));
        colorCode = colorCodeRepository.save(colorCode);

        Preferences preferences = new Preferences();
        preferences.setUser(user);
        preferences.setColorCode(colorCode);
        preferences.setNotificacionesCorreo(false);
        preferencesRepository.save(preferences);
    }
}
