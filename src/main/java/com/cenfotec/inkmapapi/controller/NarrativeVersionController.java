package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.NarrativeVersionResponseDTO;
import com.cenfotec.inkmapapi.service.NarrativeVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/narratives/{narrativeId}/versions")
@RequiredArgsConstructor
public class NarrativeVersionController {

    private final NarrativeVersionService service;

    @PostMapping
    public ResponseEntity<NarrativeVersionResponseDTO> createVersion(
            @PathVariable Long projectId,
            @PathVariable Long narrativeId,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createVersion(authentication.getName(), projectId, narrativeId));
    }

    @GetMapping
    public ResponseEntity<List<NarrativeVersionResponseDTO>> listVersions(
            @PathVariable Long projectId,
            @PathVariable Long narrativeId,
            Authentication authentication) {
        return ResponseEntity.ok(service.listVersions(authentication.getName(), projectId, narrativeId));
    }
}