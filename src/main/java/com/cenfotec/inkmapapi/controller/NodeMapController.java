package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.CreateNodeMapRequestDTO;
import com.cenfotec.inkmapapi.dto.NodeMapResponseDTO;
import com.cenfotec.inkmapapi.dto.PagedNodeMapResponseDTO;
import com.cenfotec.inkmapapi.dto.UpdateNodeMapRequestDTO;
import com.cenfotec.inkmapapi.service.NodeMapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/node-maps")
public class NodeMapController {

    private final NodeMapService nodeMapService;

    public NodeMapController(NodeMapService nodeMapService) {
        this.nodeMapService = nodeMapService;
    }

    /**
     * Creates a new node map within a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param request        node map data (name, description)
     * @return 201 Created with the new node map
     */
    @PostMapping
    public ResponseEntity<NodeMapResponseDTO> createNodeMap(
            Authentication authentication,
            @PathVariable Long projectId,
            @RequestBody CreateNodeMapRequestDTO request) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nodeMapService.createNodeMap(email, projectId, request));
    }

    /**
     * Returns the paginated list of node maps for a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param search         optional name filter (case-insensitive)
     * @param page           zero-based page index (default 0)
     * @param size           page size (default 10)
     * @return paged list of node maps
     */
    @GetMapping
    public ResponseEntity<PagedNodeMapResponseDTO> getNodeMaps(
            Authentication authentication,
            @PathVariable Long projectId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = authentication.getName();
        return ResponseEntity.ok(nodeMapService.getNodeMaps(email, projectId, search, page, size));
    }

    /**
     * Returns a single node map by ID, validated to belong to a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param nodeMapId      ID of the node map
     * @return node map detail
     */
    @GetMapping("/{nodeMapId}")
    public ResponseEntity<NodeMapResponseDTO> getNodeMapById(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId) {
        String email = authentication.getName();
        return ResponseEntity.ok(nodeMapService.getNodeMapById(email, projectId, nodeMapId));
    }

    /**
     * Updates an existing node map within a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param nodeMapId      ID of the node map to update
     * @param request        updated node map data
     * @return updated node map
     */
    @PutMapping("/{nodeMapId}")
    public ResponseEntity<NodeMapResponseDTO> updateNodeMap(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId,
            @RequestBody UpdateNodeMapRequestDTO request) {
        String email = authentication.getName();
        return ResponseEntity.ok(nodeMapService.updateNodeMap(email, projectId, nodeMapId, request));
    }

    /**
     * Deletes a node map within a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param nodeMapId      ID of the node map to delete
     * @return 204 No Content
     */
    @DeleteMapping("/{nodeMapId}")
    public ResponseEntity<Void> deleteNodeMap(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId) {
        String email = authentication.getName();
        nodeMapService.deleteNodeMap(email, projectId, nodeMapId);
        return ResponseEntity.noContent().build();
    }
}
