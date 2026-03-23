package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.UpdatePreferencesRequestDTO;
import com.cenfotec.inkmapapi.service.PreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class PreferencesController {
    private final PreferencesService preferencesService;

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<?> updatePreferences(@PathVariable Long id, @RequestBody UpdatePreferencesRequestDTO dto) {
        return preferencesService.updatePreferences(id, dto);
    }
}
