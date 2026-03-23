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

@Service
@RequiredArgsConstructor
public class PreferencesService {

    private final PreferencesRepository preferencesRepository;
    private final ColorCodeRepository colorCodeRepository;

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
