package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.CreateNodeMapRequestDTO;
import com.cenfotec.inkmapapi.dto.NodeMapResponseDTO;
import com.cenfotec.inkmapapi.dto.PagedNodeMapResponseDTO;
import com.cenfotec.inkmapapi.dto.UpdateNodeMapRequestDTO;
import com.cenfotec.inkmapapi.models.NodeMap;
import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.NodeMapRepository;
import com.cenfotec.inkmapapi.repository.ProjectRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NodeMapService {

    private final NodeMapRepository nodeMapRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public NodeMapService(NodeMapRepository nodeMapRepository,
                          ProjectRepository projectRepository,
                          UserRepository userRepository) {
        this.nodeMapRepository = nodeMapRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new node map within a project owned by the authenticated user.
     *
     * @param email     email extracted from the JWT subject (authentication.getName())
     * @param projectId ID of the project to add the node map to
     * @param request   node map data (name, description)
     * @return created node map DTO
     */
    public NodeMapResponseDTO createNodeMap(String email, Long projectId, CreateNodeMapRequestDTO request) {
        validateCreateRequest(request);

        Project project = resolveProject(email, projectId);

        NodeMap nodeMap = new NodeMap();
        nodeMap.setName(request.getName().trim());
        nodeMap.setDescription(request.getDescription());
        nodeMap.setProject(project);

        return toDTO(nodeMapRepository.save(nodeMap));
    }

    /**
     * Returns a paginated list of node maps for a project owned by the authenticated user.
     * Optionally filters by name (case-insensitive).
     *
     * @param email     email extracted from the JWT subject (authentication.getName())
     * @param projectId ID of the project
     * @param search    optional name filter
     * @param page      zero-based page index
     * @param size      number of items per page
     * @return paged response DTO
     */
    public PagedNodeMapResponseDTO getNodeMaps(String email, Long projectId,
                                               String search, int page, int size) {
        Project project = resolveProject(email, projectId);

        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        PageRequest pageable = PageRequest.of(page, size);

        Page<NodeMap> resultPage = (normalizedSearch == null)
                ? nodeMapRepository.findByProjectOrderByCreatedAtDesc(project, pageable)
                : nodeMapRepository.findByProjectAndNameContainingIgnoreCaseOrderByCreatedAtDesc(
                        project, normalizedSearch, pageable);

        return new PagedNodeMapResponseDTO(
                resultPage.getContent().stream().map(this::toDTO).toList(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isLast()
        );
    }

    /**
     * Returns a single node map by ID, validated to belong to a project owned by the authenticated user.
     *
     * @param email     email extracted from the JWT subject (authentication.getName())
     * @param projectId ID of the project
     * @param nodeMapId ID of the node map
     * @return node map DTO
     */
    public NodeMapResponseDTO getNodeMapById(String email, Long projectId, Long nodeMapId) {
        Project project = resolveProject(email, projectId);

        NodeMap nodeMap = nodeMapRepository.findByIdAndProject(nodeMapId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node map not found"));

        return toDTO(nodeMap);
    }

    /**
     * Updates an existing node map within a project owned by the authenticated user.
     *
     * @param email     email extracted from the JWT subject
     * @param projectId ID of the project
     * @param nodeMapId ID of the node map to update
     * @param request   updated node map data
     * @return updated node map DTO
     */
    public NodeMapResponseDTO updateNodeMap(String email, Long projectId,
                                            Long nodeMapId, UpdateNodeMapRequestDTO request) {
        validateUpdateRequest(request);

        Project project = resolveProject(email, projectId);

        NodeMap nodeMap = nodeMapRepository.findByIdAndProject(nodeMapId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node map not found"));

        nodeMap.setName(request.getName().trim());
        nodeMap.setDescription(request.getDescription());

        return toDTO(nodeMapRepository.save(nodeMap));
    }

    /**
     * Deletes a node map within a project owned by the authenticated user.
     *
     * @param email     email extracted from the JWT subject
     * @param projectId ID of the project
     * @param nodeMapId ID of the node map to delete
     */
    public void deleteNodeMap(String email, Long projectId, Long nodeMapId) {
        Project project = resolveProject(email, projectId);

        NodeMap nodeMap = nodeMapRepository.findByIdAndProject(nodeMapId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node map not found"));

        nodeMapRepository.delete(nodeMap);
    }

    /**
     * Resolves and validates that the project exists and belongs to the authenticated user.
     *
     * @param email     email extracted from the JWT subject
     * @param projectId ID of the project
     * @return the validated Project entity
     */
    private Project resolveProject(String email, Long projectId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        if (!project.getUser().getEmail().equals(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access this project");
        }

        return project;
    }

    private void validateCreateRequest(CreateNodeMapRequestDTO request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
    }

    private void validateUpdateRequest(UpdateNodeMapRequestDTO request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
    }

    private NodeMapResponseDTO toDTO(NodeMap nm) {
        return new NodeMapResponseDTO(
                nm.getId(),
                nm.getName(),
                nm.getDescription(),
                nm.getCreatedAt(),
                nm.getUpdatedAt()
        );
    }
}
