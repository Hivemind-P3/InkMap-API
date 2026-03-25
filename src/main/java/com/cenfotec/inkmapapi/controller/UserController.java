package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.UpdatePreferencesRequestDTO;
import com.cenfotec.inkmapapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long id, @RequestBody UpdatePreferencesRequestDTO dto) {
        return userService.updateUser(id, dto);
    }
}
