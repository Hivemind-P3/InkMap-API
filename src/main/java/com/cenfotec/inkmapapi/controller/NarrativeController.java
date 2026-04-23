package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.*;
import com.cenfotec.inkmapapi.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/narratives")
@RequiredArgsConstructor
public class NarrativeController {

    private final NarrativeService service;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CreateNarrativeDTO dto,
                                   Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(service.create(dto, username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id,
                                    @RequestBody UpdateNarrativeDTO dto,
                                    Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(service.edit(id, dto, username));
    }

    @GetMapping("/projects/{idProyecto}")
    public ResponseEntity<?> listar(@PathVariable Long idProyecto,
                                    Authentication authentication) {
        return ResponseEntity.ok(service.listByProject(idProyecto));
    }

    @PutMapping("/order")
    public ResponseEntity<?> reordenar(@RequestBody NarrativeOrderDTO dto,
                                       Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(service.reorder(dto, username));
    }

    @GetMapping("/projects/{idProyecto}/search")
    public ResponseEntity<?> search(@PathVariable Long idProyecto,
                                    @RequestParam String q,
                                    Authentication authentication) {
        return ResponseEntity.ok(service.search(idProyecto, q, authentication.getName()));
    }
}