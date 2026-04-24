package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.CreateNodeRequestDTO;
import com.cenfotec.inkmapapi.dto.NodeResponseDTO;
import com.cenfotec.inkmapapi.dto.PagedNodeResponseDTO;
import com.cenfotec.inkmapapi.dto.UpdateNodeRequestDTO;
import com.cenfotec.inkmapapi.service.NodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/node-maps/{nodeMapId}/nodes")
public class NodeController {

    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    /**
     * Creates a new node within a node map belonging to a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param nodeMapId      ID of the node map
     * @param request        node data (label, description, posX, posY)
     * @return 201 Created with the new node
     */
    @PostMapping
    public ResponseEntity<NodeResponseDTO> createNode(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId,
            @RequestBody CreateNodeRequestDTO request) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nodeService.createNode(email, projectId, nodeMapId, request));
    }

    /**
     * Returns the paginated list of nodes for a node map belonging to a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param nodeMapId      ID of the node map
     * @param page           zero-based page index (default 0)
     * @param size           page size (default 20)
     * @return paged list of nodes
     */
    @GetMapping
    public ResponseEntity<PagedNodeResponseDTO> getNodes(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String email = authentication.getName();
        return ResponseEntity.ok(nodeService.getNodes(email, projectId, nodeMapId, page, size));
    }

    /**
     * Returns a single node by ID, validated to belong to the given node map.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param nodeMapId      ID of the node map
     * @param nodeId         ID of the node
     * @return node detail
     */
    @GetMapping("/{nodeId}")
    public ResponseEntity<NodeResponseDTO> getNodeById(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId,
            @PathVariable Long nodeId) {
        String email = authentication.getName();
        return ResponseEntity.ok(nodeService.getNodeById(email, projectId, nodeMapId, nodeId));
    }

    /**
     * Updates an existing node within a node map belonging to a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param nodeMapId      ID of the node map
     * @param nodeId         ID of the node to update
     * @param request        updated node data
     * @return updated node
     */
    @PutMapping("/{nodeId}")
    public ResponseEntity<NodeResponseDTO> updateNode(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId,
            @PathVariable Long nodeId,
            @RequestBody UpdateNodeRequestDTO request) {
        String email = authentication.getName();
        return ResponseEntity.ok(nodeService.updateNode(email, projectId, nodeMapId, nodeId, request));
    }

    /**
     * Deletes a node within a node map belonging to a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param nodeMapId      ID of the node map
     * @param nodeId         ID of the node to delete
     * @return 204 No Content
     */
    @DeleteMapping("/{nodeId}")
    public ResponseEntity<Void> deleteNode(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long nodeMapId,
            @PathVariable Long nodeId) {
        String email = authentication.getName();
        nodeService.deleteNode(email, projectId, nodeMapId, nodeId);
        return ResponseEntity.noContent().build();
    }
}