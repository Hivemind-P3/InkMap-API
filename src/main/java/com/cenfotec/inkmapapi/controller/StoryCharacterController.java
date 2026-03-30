package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.CreateStoryCharacterRequestDTO;
import com.cenfotec.inkmapapi.dto.PagedStoryCharacterResponseDTO;
import com.cenfotec.inkmapapi.dto.StoryCharacterResponseDTO;
import com.cenfotec.inkmapapi.service.StoryCharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/characters")
public class StoryCharacterController {

    private final StoryCharacterService storyCharacterService;

    public StoryCharacterController(StoryCharacterService storyCharacterService) {
        this.storyCharacterService = storyCharacterService;
    }

    /**
     * Creates a new character within a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param request        character data (name, role, description, age, gender, race)
     * @return 201 Created with the new character
     */
    @PostMapping
    public ResponseEntity<StoryCharacterResponseDTO> createCharacter(
            Authentication authentication,
            @PathVariable Long projectId,
            @RequestBody CreateStoryCharacterRequestDTO request) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storyCharacterService.createCharacter(email, projectId, request));
    }

    /**
     * Returns the paginated list of characters for a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param search         optional name filter (case-insensitive)
     * @param page           zero-based page index (default 0)
     * @param size           page size (default 10)
     * @return paged list of characters
     */
    @GetMapping
    public ResponseEntity<PagedStoryCharacterResponseDTO> getCharacters(
            Authentication authentication,
            @PathVariable Long projectId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = authentication.getName();
        return ResponseEntity.ok(storyCharacterService.getCharacters(email, projectId, search, page, size));
    }

    /**
     * Returns a single character by ID, validated to belong to a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param characterId    ID of the character
     * @return character detail
     */
    @GetMapping("/{characterId}")
    public ResponseEntity<StoryCharacterResponseDTO> getCharacterById(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long characterId) {
        String email = authentication.getName();
        return ResponseEntity.ok(storyCharacterService.getCharacterById(email, projectId, characterId));
    }
}
