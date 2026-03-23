package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.CreateProjectRequestDTO;
import com.cenfotec.inkmapapi.dto.PagedProjectResponseDTO;
import com.cenfotec.inkmapapi.dto.ProjectResponseDTO;
import com.cenfotec.inkmapapi.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Returns the authenticated user's projects, paginated and optionally filtered by title or tag.
     * The user is resolved from the JWT subject — no userId is accepted from the client.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param search         optional search term (matches title or tags, case-insensitive)
     * @param page           zero-based page index (default 0)
     * @param size           page size (default 10)
     * @return paged list of projects
     */
    @GetMapping("/me")
    public ResponseEntity<PagedProjectResponseDTO> getMyProjects(
            Authentication authentication,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = authentication.getName();
        return ResponseEntity.ok(projectService.getProjectsForAuthenticatedUser(email, search, page, size));
    }

    /**
     * Creates a new project for the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param request        project data (title, description, medium, tags)
     * @return 201 Created with the new project, or error if validation/conflict fails
     */
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(Authentication authentication,
                                                            @RequestBody CreateProjectRequestDTO request) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(email, request));
    }
}
