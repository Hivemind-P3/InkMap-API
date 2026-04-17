package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.WikiResponseDTO;
import com.cenfotec.inkmapapi.models.Node;
import com.cenfotec.inkmapapi.models.NodeMap;
import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.models.Wiki;
import com.cenfotec.inkmapapi.repository.NodeMapRepository;
import com.cenfotec.inkmapapi.repository.NodeRepository;
import com.cenfotec.inkmapapi.repository.ProjectRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import com.cenfotec.inkmapapi.repository.WikiRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NodeWikiService {

    private final NodeRepository nodeRepository;
    private final NodeMapRepository nodeMapRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final WikiRepository wikiRepository;

    public NodeWikiService(NodeRepository nodeRepository,
                           NodeMapRepository nodeMapRepository,
                           ProjectRepository projectRepository,
                           UserRepository userRepository,
                           WikiRepository wikiRepository) {
        this.nodeRepository = nodeRepository;
        this.nodeMapRepository = nodeMapRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.wikiRepository = wikiRepository;
    }

    /**
     * Returns the list of wikis associated with a node.
     */
    @Transactional(readOnly = true)
    public List<WikiResponseDTO> getWikisForNode(String email, Long projectId,
                                                  Long nodeMapId, Long nodeId) {
        Node node = resolveNode(email, projectId, nodeMapId, nodeId);
        return node.getWikis().stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Associates an existing wiki to a node.
     * Both must belong to the same project. Duplicate associations are rejected.
     */
    @Transactional
    public void addWikiToNode(String email, Long projectId,
                               Long nodeMapId, Long nodeId, Long wikiId) {
        Project project = resolveProject(email, projectId);

        NodeMap nodeMap = nodeMapRepository.findByIdAndProject(nodeMapId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node map not found"));

        Node node = nodeRepository.findByIdAndNodeMap(nodeId, nodeMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node not found"));

        Wiki wiki = wikiRepository.findByIdAndProject(wikiId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Wiki not found or does not belong to this project"));

        boolean alreadyLinked = node.getWikis().stream()
                .anyMatch(w -> w.getId().equals(wiki.getId()));
        if (alreadyLinked) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Wiki is already associated with this node");
        }

        node.getWikis().add(wiki);
        nodeRepository.save(node);
    }

    /**
     * Removes a wiki association from a node.
     */
    @Transactional
    public void removeWikiFromNode(String email, Long projectId,
                                    Long nodeMapId, Long nodeId, Long wikiId) {
        Project project = resolveProject(email, projectId);

        NodeMap nodeMap = nodeMapRepository.findByIdAndProject(nodeMapId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node map not found"));

        Node node = nodeRepository.findByIdAndNodeMap(nodeId, nodeMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node not found"));

        boolean removed = node.getWikis().removeIf(w -> w.getId().equals(wikiId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wiki is not associated with this node");
        }

        nodeRepository.save(node);
    }

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

    private Node resolveNode(String email, Long projectId, Long nodeMapId, Long nodeId) {
        Project project = resolveProject(email, projectId);

        NodeMap nodeMap = nodeMapRepository.findByIdAndProject(nodeMapId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node map not found"));

        return nodeRepository.findByIdAndNodeMap(nodeId, nodeMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node not found"));
    }

    private WikiResponseDTO toDTO(Wiki w) {
        return new WikiResponseDTO(
                w.getId(),
                w.getTitle(),
                w.getContent(),
                w.getProject().getId(),
                w.getCreatedAt(),
                w.getUpdatedAt()
        );
    }
}
