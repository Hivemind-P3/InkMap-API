package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.CreateStoryCharacterRequestDTO;
import com.cenfotec.inkmapapi.dto.PagedStoryCharacterResponseDTO;
import com.cenfotec.inkmapapi.dto.StoryCharacterResponseDTO;
import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.StoryCharacter;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.ProjectRepository;
import com.cenfotec.inkmapapi.repository.StoryCharacterRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StoryCharacterService {

    private final StoryCharacterRepository storyCharacterRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public StoryCharacterService(StoryCharacterRepository storyCharacterRepository,
                                 ProjectRepository projectRepository,
                                 UserRepository userRepository) {
        this.storyCharacterRepository = storyCharacterRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new character within a project owned by the authenticated user.
     *
     * @param email     email extracted from the JWT subject (authentication.getName())
     * @param projectId ID of the project to add the character to
     * @param request   character data
     * @return created character DTO
     */
    public StoryCharacterResponseDTO createCharacter(String email, Long projectId,
                                                     CreateStoryCharacterRequestDTO request) {
        validateCreateRequest(request);

        Project project = resolveProject(email, projectId);

        StoryCharacter character = new StoryCharacter();
        character.setName(request.getName().trim());
        character.setRole(request.getRole());
        character.setDescription(request.getDescription());
        character.setAge(request.getAge());
        character.setGender(request.getGender());
        character.setRace(request.getRace());
        character.setProject(project);

        return toDTO(storyCharacterRepository.save(character));
    }

    /**
     * Returns a paginated list of characters for a project owned by the authenticated user.
     * Optionally filters by name (case-insensitive).
     *
     * @param email     email extracted from the JWT subject (authentication.getName())
     * @param projectId ID of the project
     * @param search    optional name filter
     * @param page      zero-based page index (default 0)
     * @param size      number of items per page (default 10)
     * @return paged response DTO
     */
    public PagedStoryCharacterResponseDTO getCharacters(String email, Long projectId,
                                                        String search, int page, int size) {
        Project project = resolveProject(email, projectId);

        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        PageRequest pageable = PageRequest.of(page, size);

        Page<StoryCharacter> resultPage = (normalizedSearch == null)
                ? storyCharacterRepository.findByProjectOrderByCreatedAtDesc(project, pageable)
                : storyCharacterRepository.findByProjectAndNameContainingIgnoreCaseOrderByCreatedAtDesc(
                        project, normalizedSearch, pageable);

        return new PagedStoryCharacterResponseDTO(
                resultPage.getContent().stream().map(this::toDTO).toList(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isLast()
        );
    }

    /**
     * Returns a single character by ID, validated to belong to a project owned by the authenticated user.
     *
     * @param email       email extracted from the JWT subject (authentication.getName())
     * @param projectId   ID of the project
     * @param characterId ID of the character
     * @return character DTO
     */
    public StoryCharacterResponseDTO getCharacterById(String email, Long projectId, Long characterId) {
        Project project = resolveProject(email, projectId);

        StoryCharacter character = storyCharacterRepository.findByIdAndProject(characterId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found"));

        return toDTO(character);
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

    private void validateCreateRequest(CreateStoryCharacterRequestDTO request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (request.getGender() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gender is required");
        }
        if (request.getAge() != null && request.getAge() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Age must be 0 or greater");
        }
    }

    private StoryCharacterResponseDTO toDTO(StoryCharacter c) {
        return new StoryCharacterResponseDTO(
                c.getId(),
                c.getName(),
                c.getRole(),
                c.getDescription(),
                c.getAge(),
                c.getGender(),
                c.getRace(),
                c.getCreatedAt()
        );
    }
}
