package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Actualiza los campos de name, email y password del usuario
     *
     * @param id ID del usuario que se quiere actualizar
     * @param user datos del usuario por actualizar
     * @return retorna el usuario guardado
     */
    public ResponseEntity<?> updateUser(Long id, User user) {
        Optional<User> existingUser = userRepository.findById(id);

        if(existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + "not found");
        }

        Optional<User> emailOwner = userRepository.findByEmail(user.getEmail());
        if (emailOwner.isPresent() && !emailOwner.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use");
        }

        User updatedUser = existingUser.get();
        updatedUser.setName(user.getName());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(updatedUser);
        return ResponseEntity.ok(savedUser);
    }
}
