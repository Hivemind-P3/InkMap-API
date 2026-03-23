package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.CreateProjectRequestDTO;
import com.cenfotec.inkmapapi.dto.ProjectResponseDTO;
import com.cenfotec.inkmapapi.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Returns the authenticated user's projects, ordered by creation date descending.
     * The user is resolved from the JWT subject — no userId is accepted from the client.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @return list of projects belonging to the authenticated user
     */
    @GetMapping("/me")
    public ResponseEntity<List<ProjectResponseDTO>> getMyProjects(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(projectService.getProjectsForAuthenticatedUser(email));
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
