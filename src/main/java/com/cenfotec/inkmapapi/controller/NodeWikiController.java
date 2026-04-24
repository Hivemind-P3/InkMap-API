package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.WikiResponseDTO;
import com.cenfotec.inkmapapi.service.NodeWikiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/node-maps/{nodeMapId}/nodes/{nodeId}/wikis")
public class NodeWikiController {

    private final NodeWikiService nodeWikiService;

    public NodeWikiController(NodeWikiService nodeWikiService) {
        this.nodeWikiService = nodeWikiService;
    }

    @GetMapping
    public ResponseEntity<List<WikiResponseDTO>> getWikisForNode(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId,
            @PathVariable Long nodeId) {
        String email = authentication.getName();
        return ResponseEntity.ok(nodeWikiService.getWikisForNode(email, projectId, nodeMapId, nodeId));
    }

    @PostMapping("/{wikiId}")
    public ResponseEntity<Void> addWikiToNode(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId,
            @PathVariable Long nodeId,
            @PathVariable Long wikiId) {
        String email = authentication.getName();
        nodeWikiService.addWikiToNode(email, projectId, nodeMapId, nodeId, wikiId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{wikiId}")
    public ResponseEntity<Void> removeWikiFromNode(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId,
            @PathVariable Long nodeId,
            @PathVariable Long wikiId) {
        String email = authentication.getName();
        nodeWikiService.removeWikiFromNode(email, projectId, nodeMapId, nodeId, wikiId);
        return ResponseEntity.noContent().build();
    }
}
