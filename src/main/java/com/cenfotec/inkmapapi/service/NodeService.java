package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.CreateNodeRequestDTO;
import com.cenfotec.inkmapapi.dto.NodeResponseDTO;
import com.cenfotec.inkmapapi.dto.PagedNodeResponseDTO;
import com.cenfotec.inkmapapi.dto.UpdateNodeRequestDTO;
import com.cenfotec.inkmapapi.models.Node;
import com.cenfotec.inkmapapi.models.NodeMap;
import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.NodeMapRepository;
import com.cenfotec.inkmapapi.repository.NodeRepository;
import com.cenfotec.inkmapapi.repository.ProjectRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NodeService {

    private final NodeRepository nodeRepository;
    private final NodeMapRepository nodeMapRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public NodeService(NodeRepository nodeRepository,
                       NodeMapRepository nodeMapRepository,
                       ProjectRepository projectRepository,
                       UserRepository userRepository) {
        this.nodeRepository = nodeRepository;
        this.nodeMapRepository = nodeMapRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new node within a node map belonging to a project owned by the authenticated user.
     *
     * @param email     email extracted from the JWT subject
     * @param projectId ID of the project
     * @param nodeMapId ID of the node map
     * @param request   node data (label, description, posX, posY)
     * @return created node DTO
     */
    public NodeResponseDTO createNode(String email, Long projectId, Long nodeMapId, CreateNodeRequestDTO request) {
        validateCreateRequest(request);

        NodeMap nodeMap = resolveNodeMap(email, projectId, nodeMapId);

        Node node = new Node();
        node.setLabel(request.getLabel().trim());
        node.setDescription(request.getDescription());
        node.setPosX(request.getPosX());
        node.setPosY(request.getPosY());
        node.setNodeMap(nodeMap);

        return toDTO(nodeRepository.save(node));
    }

    /**
     * Returns a paginated list of nodes for a node map belonging to a project owned by the authenticated user.
     *
     * @param email     email extracted from the JWT subject
     * @param projectId ID of the project
     * @param nodeMapId ID of the node map
     * @param page      zero-based page index
     * @param size      number of items per page
     * @return paged response DTO
     */
    public PagedNodeResponseDTO getNodes(String email, Long projectId, Long nodeMapId, int page, int size) {
        NodeMap nodeMap = resolveNodeMap(email, projectId, nodeMapId);

        PageRequest pageable = PageRequest.of(page, size);
        Page<Node> resultPage = nodeRepository.findByNodeMapOrderByIdAsc(nodeMap, pageable);

        return new PagedNodeResponseDTO(
                resultPage.getContent().stream().map(this::toDTO).toList(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isLast()
        );
    }

    /**
     * Returns a single node by ID, validated to belong to the given node map.
     *
     * @param email     email extracted from the JWT subject
     * @param projectId ID of the project
     * @param nodeMapId ID of the node map
     * @param nodeId    ID of the node
     * @return node DTO
     */
    public NodeResponseDTO getNodeById(String email, Long projectId, Long nodeMapId, Long nodeId) {
        NodeMap nodeMap = resolveNodeMap(email, projectId, nodeMapId);

        Node node = nodeRepository.findByIdAndNodeMap(nodeId, nodeMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node not found"));

        return toDTO(node);
    }

    /**
     * Updates an existing node within a node map belonging to a project owned by the authenticated user.
     *
     * @param email     email extracted from the JWT subject
     * @param projectId ID of the project
     * @param nodeMapId ID of the node map
     * @param nodeId    ID of the node to update
     * @param request   updated node data
     * @return updated node DTO
     */
    public NodeResponseDTO updateNode(String email, Long projectId, Long nodeMapId,
                                      Long nodeId, UpdateNodeRequestDTO request) {
        validateUpdateRequest(request);

        NodeMap nodeMap = resolveNodeMap(email, projectId, nodeMapId);

        Node node = nodeRepository.findByIdAndNodeMap(nodeId, nodeMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node not found"));

        node.setLabel(request.getLabel().trim());
        node.setDescription(request.getDescription());
        node.setPosX(request.getPosX());
        node.setPosY(request.getPosY());

        return toDTO(nodeRepository.save(node));
    }

    /**
     * Deletes a node within a node map belonging to a project owned by the authenticated user.
     *
     * @param email     email extracted from the JWT subject
     * @param projectId ID of the project
     * @param nodeMapId ID of the node map
     * @param nodeId    ID of the node to delete
     */
    public void deleteNode(String email, Long projectId, Long nodeMapId, Long nodeId) {
        NodeMap nodeMap = resolveNodeMap(email, projectId, nodeMapId);

        Node node = nodeRepository.findByIdAndNodeMap(nodeId, nodeMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node not found"));

        nodeRepository.delete(node);
    }

    /**
     * Resolves and validates the ownership chain: user → project → node map.
     *
     * @param email     email extracted from the JWT subject
     * @param projectId ID of the project
     * @param nodeMapId ID of the node map
     * @return the validated NodeMap entity
     */
    private NodeMap resolveNodeMap(String email, Long projectId, Long nodeMapId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        if (!project.getUser().getEmail().equals(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access this project");
        }

        return nodeMapRepository.findByIdAndProject(nodeMapId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node map not found"));
    }

    private void validateCreateRequest(CreateNodeRequestDTO request) {
        if (request.getLabel() == null || request.getLabel().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Label is required");
        }
        if (request.getPosX() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "posX is required");
        }
        if (request.getPosY() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "posY is required");
        }
    }

    private void validateUpdateRequest(UpdateNodeRequestDTO request) {
        if (request.getLabel() == null || request.getLabel().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Label is required");
        }
        if (request.getPosX() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "posX is required");
        }
        if (request.getPosY() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "posY is required");
        }
    }

    private NodeResponseDTO toDTO(Node node) {
        return new NodeResponseDTO(
                node.getId(),
                node.getLabel(),
                node.getDescription(),
                node.getPosX(),
                node.getPosY(),
                node.getNodeMap().getId()
        );
    }
}