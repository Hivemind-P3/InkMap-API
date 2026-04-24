package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.CreateNodeRelationRequestDTO;
import com.cenfotec.inkmapapi.dto.NodeRelationResponseDTO;
import com.cenfotec.inkmapapi.service.NodeRelationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/node-maps/{nodeMapId}/relations")
public class NodeRelationController {

    private final NodeRelationService nodeRelationService;

    public NodeRelationController(NodeRelationService nodeRelationService) {
        this.nodeRelationService = nodeRelationService;
    }

    /**
     * Creates a relation between two nodes in the same node map.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param nodeMapId      ID of the node map
     * @param request        sourceNodeId and targetNodeId
     * @return 201 Created with the new relation
     */
    @PostMapping
    public ResponseEntity<NodeRelationResponseDTO> createRelation(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId,
            @RequestBody CreateNodeRelationRequestDTO request) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nodeRelationService.createRelation(email, projectId, nodeMapId, request));
    }

    /**
     * Lists all relations for a node map belonging to a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param nodeMapId      ID of the node map
     * @return list of relations
     */
    @GetMapping
    public ResponseEntity<List<NodeRelationResponseDTO>> getRelations(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId) {
        String email = authentication.getName();
        return ResponseEntity.ok(nodeRelationService.getRelations(email, projectId, nodeMapId));
    }

    /**
     * Deletes a relation within a node map belonging to a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param nodeMapId      ID of the node map
     * @param relationId     ID of the relation to delete
     * @return 204 No Content
     */
    @DeleteMapping("/{relationId}")
    public ResponseEntity<Void> deleteRelation(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId,
            @PathVariable Long relationId) {
        String email = authentication.getName();
        nodeRelationService.deleteRelation(email, projectId, nodeMapId, relationId);
        return ResponseEntity.noContent().build();
    }
}
