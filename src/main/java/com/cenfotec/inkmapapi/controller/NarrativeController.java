package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.*;
import com.cenfotec.inkmapapi.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/narratives")
@RequiredArgsConstructor
@Transactional
public class NarrativeController {

    private final NarrativeService service;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CreateNarrativeDTO dto,
                                   @RequestAttribute("idUsuario") Long idUsuario) {
        return ResponseEntity.ok(service.create(dto, idUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id,
                                    @RequestBody UpdateNarrativeDTO dto,
                                    @RequestAttribute("idUsuario") Long idUsuario) {
        return ResponseEntity.ok(service.edit(id, dto, idUsuario));
    }

    @GetMapping("/projects/{idProyecto}")
    public ResponseEntity<?> listar(@PathVariable Long idProyecto) {
        return ResponseEntity.ok(service.listByProject(idProyecto));
    }

    @PutMapping("/order")
    public ResponseEntity<?> reordenar(@RequestBody NarrativeOrderDTO dto,
                                       @RequestAttribute("idUsuario") Long idUsuario) {
        return ResponseEntity.ok(service.reorder(dto, idUsuario));
    }
}