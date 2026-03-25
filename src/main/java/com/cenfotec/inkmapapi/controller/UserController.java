package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.UpdateRoleDTO;
import com.cenfotec.inkmapapi.dto.UserResponseDTO;
import com.cenfotec.inkmapapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponseDTO> updateRole(@PathVariable Long id,
                                                      @RequestBody UpdateRoleDTO dto,
                                                      Authentication auth) {
        return ResponseEntity.ok(userService.updateRole(id, dto.getRole(), auth.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication auth) {
        userService.deleteUser(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
