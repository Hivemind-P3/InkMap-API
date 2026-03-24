package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.UpdateRoleDTO;
import com.cenfotec.inkmapapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/{id}/role")
    public void updateRole(@PathVariable Long id,
                           @RequestBody UpdateRoleDTO dto,
                           Authentication auth) {

        userService.updateRole(id, dto.getRole(), auth.getName());
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id, Authentication auth) {
        userService.deleteUser(id, auth.getName());
    }
}