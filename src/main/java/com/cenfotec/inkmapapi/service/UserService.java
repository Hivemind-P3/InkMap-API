package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.UpdatePreferencesRequestDTO;
import com.cenfotec.inkmapapi.models.ColorCode;
import com.cenfotec.inkmapapi.models.Preferences;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.models.enums.Role;
import com.cenfotec.inkmapapi.repository.ColorCodeRepository;
import com.cenfotec.inkmapapi.repository.PreferencesRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Servicio encargado de manejar la lógica de gestión de usuarios
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PreferencesRepository preferencesRepository;
    private final ColorCodeRepository colorCodeRepository;

    /**
     * Actualiza los campos de name, email y password del usuario y las preferencias
     *
     * @param id ID del usuario que se quiere actualizar
     * @param dto datos del usuario y preferencias por actualizar
     * @return retorna el usuario guardado
     */
    public ResponseEntity<?> updateUser(Long id, UpdatePreferencesRequestDTO dto) {
        Preferences preferences = preferencesRepository.findByUserId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preferences not found for user " + id));

        preferences.setNotificacionesCorreo(dto.isNotificacionesCorreo());

        ColorCode colorCode = preferences.getColorCode();
        colorCode.setColores(dto.getColores());
        colorCodeRepository.save(colorCode);

        preferencesRepository.save(preferences);

        Optional<User> existingUser = userRepository.findById(id);

        if(existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " not found");
        }

        Optional<User> emailOwner = userRepository.findByEmail(dto.getUser().getEmail());
        if (emailOwner.isPresent() && !emailOwner.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use");
        }

        User updatedUser = existingUser.get();
        updatedUser.setName(dto.getUser().getName());
        updatedUser.setEmail(dto.getUser().getEmail());

        if (dto.getUser().getPassword() != null && !dto.getUser().getPassword().isBlank()) {
            updatedUser.setPassword(passwordEncoder.encode(dto.getUser().getPassword()));
        }

        User savedUser = userRepository.save(updatedUser);
        return ResponseEntity.ok(savedUser);
    }

    public void updateRole(Long userId, String role, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow();

        User targetUser = userRepository.findById(userId)
                .orElseThrow();

        if (currentUser.getId().equals(targetUser.getId())) {
            throw new RuntimeException("No puedes cambiar tu propio rol");
        }

        targetUser.setRole(Role.valueOf(role));
        userRepository.save(targetUser);
    }

    public void deleteUser(Long userId, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow();

        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("No puedes borrarte a ti mismo");
        }

        userRepository.deleteById(userId);
    }
}
