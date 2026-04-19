package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.CreatePointOfInterestRequestDTO;
import com.cenfotec.inkmapapi.dto.PointOfInterestResponseDTO;
import com.cenfotec.inkmapapi.service.PointOfInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/geographic-maps/{mapId}/points-of-interest")
@RequiredArgsConstructor
public class PointOfInterestController {

    private final PointOfInterestService pointOfInterestService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PointOfInterestResponseDTO> createPointOfInterest(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long mapId,
            @RequestBody CreatePointOfInterestRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pointOfInterestService.createPointOfInterest(authentication.getName(), projectId, mapId, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PointOfInterestResponseDTO>> getPointsOfInterest(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long mapId) {
        return ResponseEntity.ok(
                pointOfInterestService.getPointsOfInterest(authentication.getName(), projectId, mapId));
    }

    @DeleteMapping("/{poiId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deletePointOfInterest(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long mapId,
            @PathVariable Long poiId) {
        pointOfInterestService.deletePointOfInterest(authentication.getName(), projectId, mapId, poiId);
        return ResponseEntity.noContent().build();
    }
}
