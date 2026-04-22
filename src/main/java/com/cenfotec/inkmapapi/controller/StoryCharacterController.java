package com.cenfotec.inkmapapi.controller;

import com.cenfotec.inkmapapi.dto.CreateStoryCharacterRequestDTO;
import com.cenfotec.inkmapapi.dto.GenerateCharacterRequestDTO;
import com.cenfotec.inkmapapi.dto.PagedStoryCharacterResponseDTO;
import com.cenfotec.inkmapapi.dto.StoryCharacterResponseDTO;
import com.cenfotec.inkmapapi.dto.UpdateStoryCharacterRequestDTO;
import com.cenfotec.inkmapapi.service.GroqCharacterService;
import com.cenfotec.inkmapapi.service.ProjectContextService;
import com.cenfotec.inkmapapi.service.StoryCharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/projects/{projectId}/characters")
public class StoryCharacterController {

    private final StoryCharacterService storyCharacterService;
    private final ProjectContextService projectContextService;
    private final GroqCharacterService groqCharacterService;

    public StoryCharacterController(StoryCharacterService storyCharacterService,
                                    ProjectContextService projectContextService,
                                    GroqCharacterService groqCharacterService) {
        this.storyCharacterService = storyCharacterService;
        this.projectContextService = projectContextService;
        this.groqCharacterService = groqCharacterService;
    }

    /**
     * Creates a new character within a project owned by the authenticated user.
     *
     * @param authentication injected by Spring Security from the validated JWT
     * @param projectId      ID of the project
     * @param request        character data (name, role, description, age, gender, race)
     * @return 201 Created with the new character
     */
    @PostMapping("/generate")
    public ResponseEntity<CreateStoryCharacterRequestDTO> generateCharacter(
            Authentication authentication,
            @PathVariable Long projectId,
            @RequestBody GenerateCharacterRequestDTO request) {
        if (request.getInstructions() == null || request.getInstructions().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Instructions are required");
        }
        String email = authentication.getName();
        String context = projectContextService.buildContext(email, projectId);
        return ResponseEntity.ok(groqCharacterService.generate(context, request.getInstructions()));
    }

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

    @PutMapping("/{characterId}")
    public ResponseEntity<StoryCharacterResponseDTO> updateCharacter(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long characterId,
            @RequestBody UpdateStoryCharacterRequestDTO request) {
        String email = authentication.getName();
        return ResponseEntity.ok(storyCharacterService.updateCharacter(email, projectId, characterId, request));
    }

    @DeleteMapping("/{characterId}")
    public ResponseEntity<Void> deleteCharacter(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long characterId) {
        String email = authentication.getName();
        storyCharacterService.deleteCharacter(email, projectId, characterId);
        return ResponseEntity.noContent().build();
    }
}
