package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.CreateProjectRequestDTO;
import com.cenfotec.inkmapapi.dto.PagedProjectResponseDTO;
import com.cenfotec.inkmapapi.dto.ProjectResponseDTO;
import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.ProjectRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    /**
     * Returns a paginated list of projects for the authenticated user.
     * Optionally filters by title or tag (case-insensitive).
     * Routes to a simple derived query when search is absent to avoid
     * collection-join + pagination issues in Hibernate 6.
     *
     * @param email  email extracted from the JWT subject (authentication.getName())
     * @param search optional search term matched against title and tags
     * @param page   zero-based page index (default 0)
     * @param size   number of items per page (default 10)
     * @return paged response DTO
     */
    public PagedProjectResponseDTO getProjectsForAuthenticatedUser(String email, String search, int page, int size) {
        User user = resolveUser(email);

        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Project> resultPage = (normalizedSearch == null)
                ? projectRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                : projectRepository.findByUserWithSearch(user, normalizedSearch, pageable);

        return new PagedProjectResponseDTO(
                resultPage.getContent().stream().map(this::toDTO).toList(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isLast()
        );
    }

    /**
     * Creates a new project for the authenticated user.
     *
     * @param email   email extracted from the JWT subject (authentication.getName())
     * @param request project creation data (title, description, medium, tags)
     * @return created project DTO
     */
    public ProjectResponseDTO createProject(String email, CreateProjectRequestDTO request) {
        validateCreateRequest(request);

        User user = resolveUser(email);

        if (projectRepository.findByUserAndTitle(user, request.getTitle()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A project with this title already exists");
        }

        Project project = new Project();
        project.setTitle(request.getTitle().trim());
        project.setDescription(request.getDescription().trim());
        project.setMedium(request.getMedium().trim());
        project.setTags(request.getTags());
        project.setUser(user);

        return toDTO(projectRepository.save(project));
    }

    private void validateCreateRequest(CreateProjectRequestDTO request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is required");
        }
        if (request.getMedium() == null || request.getMedium().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medium is required");
        }
    }

    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    private ProjectResponseDTO toDTO(Project p) {
        return new ProjectResponseDTO(p.getId(), p.getTitle(), p.getDescription(), p.getMedium(), p.getTags(), p.getCreatedAt());
    }
}
