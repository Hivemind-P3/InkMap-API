package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.CreateNodeRelationRequestDTO;
import com.cenfotec.inkmapapi.dto.NodeRelationResponseDTO;
import com.cenfotec.inkmapapi.models.Node;
import com.cenfotec.inkmapapi.models.NodeMap;
import com.cenfotec.inkmapapi.models.NodeRelation;
import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.NodeMapRepository;
import com.cenfotec.inkmapapi.repository.NodeRelationRepository;
import com.cenfotec.inkmapapi.repository.NodeRepository;
import com.cenfotec.inkmapapi.repository.ProjectRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NodeRelationService {

    private final NodeRelationRepository nodeRelationRepository;
    private final NodeRepository nodeRepository;
    private final NodeMapRepository nodeMapRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public NodeRelationService(NodeRelationRepository nodeRelationRepository,
                               NodeRepository nodeRepository,
                               NodeMapRepository nodeMapRepository,
                               ProjectRepository projectRepository,
                               UserRepository userRepository) {
        this.nodeRelationRepository = nodeRelationRepository;
        this.nodeRepository = nodeRepository;
        this.nodeMapRepository = nodeMapRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a relation between two nodes in the same node map.
     * Validates: both nodes exist and belong to the node map, no self-relation,
     * no duplicate (undirected: A→B and B→A are the same).
     */
    public NodeRelationResponseDTO createRelation(String email, Long projectId, Long nodeMapId,
                                                  CreateNodeRelationRequestDTO request) {
        if (request.getSourceNodeId() == null || request.getTargetNodeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "sourceNodeId and targetNodeId are required");
        }

        if (request.getSourceNodeId().equals(request.getTargetNodeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A node cannot be related to itself");
        }

        NodeMap nodeMap = resolveNodeMap(email, projectId, nodeMapId);

        Node sourceNode = nodeRepository.findByIdAndNodeMap(request.getSourceNodeId(), nodeMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Source node not found in this node map"));

        Node targetNode = nodeRepository.findByIdAndNodeMap(request.getTargetNodeId(), nodeMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Target node not found in this node map"));

        // Undirected duplicate check: reject both A→B and B→A
        if (nodeRelationRepository.existsBySourceNodeAndTargetNode(sourceNode, targetNode)
                || nodeRelationRepository.existsBySourceNodeAndTargetNode(targetNode, sourceNode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A relation between these two nodes already exists");
        }

        NodeRelation relation = new NodeRelation();
        relation.setSourceNode(sourceNode);
        relation.setTargetNode(targetNode);
        relation.setNodeMap(nodeMap);

        return toDTO(nodeRelationRepository.save(relation));
    }

    /**
     * Lists all relations for a node map belonging to a project owned by the authenticated user.
     */
    public List<NodeRelationResponseDTO> getRelations(String email, Long projectId, Long nodeMapId) {
        NodeMap nodeMap = resolveNodeMap(email, projectId, nodeMapId);

        return nodeRelationRepository.findByNodeMapOrderByIdAsc(nodeMap)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Deletes a relation within a node map belonging to a project owned by the authenticated user.
     */
    public void deleteRelation(String email, Long projectId, Long nodeMapId, Long relationId) {
        NodeMap nodeMap = resolveNodeMap(email, projectId, nodeMapId);

        NodeRelation relation = nodeRelationRepository.findByIdAndNodeMap(relationId, nodeMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Relation not found"));

        nodeRelationRepository.delete(relation);
    }

    /**
     * Resolves and validates the ownership chain: user → project → node map.
     */
    private NodeMap resolveNodeMap(String email, Long projectId, Long nodeMapId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Project not found"));

        if (!project.getUser().getEmail().equals(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You do not have permission to access this project");
        }

        return nodeMapRepository.findByIdAndProject(nodeMapId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Node map not found"));
    }

    private NodeRelationResponseDTO toDTO(NodeRelation relation) {
        return new NodeRelationResponseDTO(
                relation.getId(),
                relation.getSourceNode().getId(),
                relation.getTargetNode().getId(),
                relation.getNodeMap().getId()
        );
    }
}
