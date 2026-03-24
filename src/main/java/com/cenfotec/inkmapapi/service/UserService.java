package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.models.Role;
import com.cenfotec.inkmapapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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