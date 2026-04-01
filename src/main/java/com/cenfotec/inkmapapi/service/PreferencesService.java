package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.models.ColorCode;
import com.cenfotec.inkmapapi.models.Preferences;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.ColorCodeRepository;
import com.cenfotec.inkmapapi.repository.PreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
