package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.CreateWikiRequestDTO;
import com.cenfotec.inkmapapi.dto.PagedWikiResponseDTO;
import com.cenfotec.inkmapapi.dto.UpdateWikiRequestDTO;
import com.cenfotec.inkmapapi.dto.WikiResponseDTO;
import com.cenfotec.inkmapapi.service.WikiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/wikis")
public class WikiController {

    private final WikiService wikiService;

    public WikiController(WikiService wikiService) {
        this.wikiService = wikiService;
    }

    @PostMapping
    public ResponseEntity<WikiResponseDTO> createWiki(
            Authentication authentication,
            @PathVariable Long projectId,
            @RequestBody CreateWikiRequestDTO request) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wikiService.createWiki(email, projectId, request));
    }

    @GetMapping
    public ResponseEntity<PagedWikiResponseDTO> getWikis(
            Authentication authentication,
            @PathVariable Long projectId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = authentication.getName();
        return ResponseEntity.ok(wikiService.getWikis(email, projectId, search, page, size));
    }

    @GetMapping("/{wikiId}")
    public ResponseEntity<WikiResponseDTO> getWikiById(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long wikiId) {
        String email = authentication.getName();
        return ResponseEntity.ok(wikiService.getWikiById(email, projectId, wikiId));
    }

    @PutMapping("/{wikiId}")
    public ResponseEntity<WikiResponseDTO> updateWiki(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long wikiId,
            @RequestBody UpdateWikiRequestDTO request) {
        String email = authentication.getName();
        return ResponseEntity.ok(wikiService.updateWiki(email, projectId, wikiId, request));
    }

    @DeleteMapping("/{wikiId}")
    public ResponseEntity<Void> deleteWiki(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long wikiId) {
        String email = authentication.getName();
        wikiService.deleteWiki(email, projectId, wikiId);
        return ResponseEntity.noContent().build();
    }
}
