package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.WikiResponseDTO;
import com.cenfotec.inkmapapi.service.PointOfInterestWikiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/geographic-maps/{mapId}/points-of-interest/{poiId}/wikis")
public class PointOfInterestWikiController {

    private final PointOfInterestWikiService poiWikiService;

    public PointOfInterestWikiController(PointOfInterestWikiService poiWikiService) {
        this.poiWikiService = poiWikiService;
    }

    @GetMapping
    public ResponseEntity<List<WikiResponseDTO>> getWikisForPoi(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long mapId,
            @PathVariable Long poiId) {
        String email = authentication.getName();
        return ResponseEntity.ok(poiWikiService.getWikisForPoi(email, projectId, mapId, poiId));
    }

    @PostMapping("/{wikiId}")
    public ResponseEntity<Void> addWikiToPoi(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long mapId,
            @PathVariable Long poiId,
            @PathVariable Long wikiId) {
        String email = authentication.getName();
        poiWikiService.addWikiToPoi(email, projectId, mapId, poiId, wikiId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{wikiId}")
    public ResponseEntity<Void> removeWikiFromPoi(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long mapId,
            @PathVariable Long poiId,
            @PathVariable Long wikiId) {
        String email = authentication.getName();
        poiWikiService.removeWikiFromPoi(email, projectId, mapId, poiId, wikiId);
        return ResponseEntity.noContent().build();
    }
}