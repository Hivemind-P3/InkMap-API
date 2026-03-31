package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.service.GeographicMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/geographic-maps")
@RequiredArgsConstructor
public class GeographicMapController {
    private final GeographicMapService geographicMapService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getAllGeographicMapsByProjectId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return geographicMapService.getAllGeographicalMapsByProjectId(page, size, id);
    }
}
