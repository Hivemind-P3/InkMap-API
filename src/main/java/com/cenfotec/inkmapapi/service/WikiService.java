package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.CreateWikiRequestDTO;
import com.cenfotec.inkmapapi.dto.PagedWikiResponseDTO;
import com.cenfotec.inkmapapi.dto.UpdateWikiRequestDTO;
import com.cenfotec.inkmapapi.dto.WikiResponseDTO;
import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.models.Wiki;
import com.cenfotec.inkmapapi.repository.ProjectRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import com.cenfotec.inkmapapi.repository.WikiRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WikiService {

    private final WikiRepository wikiRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public WikiService(WikiRepository wikiRepository,
                       ProjectRepository projectRepository,
                       UserRepository userRepository) {
        this.wikiRepository = wikiRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public WikiResponseDTO createWiki(String email, Long projectId, CreateWikiRequestDTO request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }

        Project project = resolveProject(email, projectId);

        Wiki wiki = new Wiki();
        wiki.setTitle(request.getTitle().trim());
        wiki.setContent(request.getContent());
        wiki.setProject(project);

        return toDTO(wikiRepository.save(wiki));
    }

    public PagedWikiResponseDTO getWikis(String email, Long projectId,
                                         String search, int page, int size) {
        Project project = resolveProject(email, projectId);

        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        PageRequest pageable = PageRequest.of(page, size);

        Page<Wiki> resultPage = (normalizedSearch == null)
                ? wikiRepository.findByProjectOrderByCreatedAtDesc(project, pageable)
                : wikiRepository.findByProjectAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                        project, normalizedSearch, pageable);

        return new PagedWikiResponseDTO(
                resultPage.getContent().stream().map(this::toDTO).toList(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isLast()
        );
    }

    public WikiResponseDTO getWikiById(String email, Long projectId, Long wikiId) {
        Project project = resolveProject(email, projectId);

        Wiki wiki = wikiRepository.findByIdAndProject(wikiId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wiki not found"));

        return toDTO(wiki);
    }

    public WikiResponseDTO updateWiki(String email, Long projectId, Long wikiId,
                                      UpdateWikiRequestDTO request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }

        Project project = resolveProject(email, projectId);

        Wiki wiki = wikiRepository.findByIdAndProject(wikiId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wiki not found"));

        wiki.setTitle(request.getTitle().trim());
        wiki.setContent(request.getContent());

        return toDTO(wikiRepository.save(wiki));
    }

    public void deleteWiki(String email, Long projectId, Long wikiId) {
        Project project = resolveProject(email, projectId);

        Wiki wiki = wikiRepository.findByIdAndProject(wikiId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wiki not found"));

        wikiRepository.delete(wiki);
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
