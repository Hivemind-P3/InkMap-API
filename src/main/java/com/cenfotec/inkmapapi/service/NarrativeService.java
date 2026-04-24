package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.*;
import com.cenfotec.inkmapapi.models.*;
import com.cenfotec.inkmapapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NarrativeService {

    private final NarrativeRepository repository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final StoryCharacterRepository storyCharacterRepository;
    private final WikiRepository wikiRepository;
    private final ObjectMapper objectMapper;
    private final GroqNarrativeSuggestionsService groqNarrativeSuggestionsService;

    public NarrativeResponseDTO create(CreateNarrativeDTO dto, String username) {

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Narrative narrative = new Narrative();
        narrative.setTitle(dto.getTitle());
        narrative.setContent("{}"); // vacío tipo Delta
        narrative.setProject(project);
        narrative.setCreationTime(LocalDateTime.now());
        narrative.setUpdateTime(LocalDateTime.now());

        int order = repository.findAllByProject_IdOrderByOrderAscIdAsc(dto.getProjectId()).size();
        narrative.setOrder(order);

        repository.save(narrative);

        return mapToDTO(narrative);
    }

    public NarrativeResponseDTO edit(Long narrativeId, UpdateNarrativeDTO dto, String username) {

        Narrative narrative = repository
                .findByIdAndProject_Id(narrativeId, dto.getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Narrative content not found"));

        if (dto.getTitle() != null) {
            if (dto.getTitle().length() < 2 || dto.getTitle().length() > 120) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid title");
            }
            narrative.setTitle(dto.getTitle());
        }

        if (isEffectivelyEmpty(dto.getContent())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Narrative content cannot be empty");
        }

        narrative.setContent(dto.getContent());
        narrative.setUpdateTime(LocalDateTime.now());

        repository.save(narrative);

        return mapToDTO(narrative);
    }

    public List<NarrativeResponseDTO> listByProject(Long projectId) {

        return repository.findAllByProject_IdOrderByOrderAscIdAsc(projectId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<NarrativeResponseDTO> reorder(NarrativeOrderDTO dto, String username) {

        List<Narrative> narratives =
                repository.findAllByProject_IdOrderByOrderAscIdAsc(dto.getProjectId());

        if (dto.getOrderedIds().size() != narratives.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "orderedIds must include all narratives of the project");
        }

        Map<Long, Narrative> map = narratives.stream()
                .collect(Collectors.toMap(Narrative::getId, c -> c));

        for (int i = 0; i < dto.getOrderedIds().size(); i++) {
            Long id = dto.getOrderedIds().get(i);

            if (!map.containsKey(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid narrative: " + id);
            }

            map.get(id).setOrder(i);
        }

        repository.saveAll(narratives);

        return dto.getOrderedIds().stream()
                .map(map::get)
                .map(this::mapToDTO)
                .toList();
    }

    public List<NarrativeSearchResultDTO> search(Long projectId, String query, String email) {
        if (query == null || query.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query cannot be empty");
        }
        if (query.trim().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query must be at least 3 characters");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        if (!project.getUser().getEmail().equals(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access this project");
        }

        String lowerQuery = query.toLowerCase();
        List<NarrativeSearchResultDTO> results = new ArrayList<>();

        for (Narrative narrative : repository.findAllByProject_IdOrderByOrderAscIdAsc(projectId)) {
            String plainText = extractTextFromDelta(narrative.getContent());
            boolean inTitle = narrative.getTitle().toLowerCase().contains(lowerQuery);
            int contentIdx = plainText.toLowerCase().indexOf(lowerQuery);

            if (!inTitle && contentIdx == -1) continue;

            String snippet;
            if (contentIdx != -1) {
                int start = Math.max(0, contentIdx - 75);
                int end = Math.min(plainText.length(), contentIdx + query.length() + 75);
                snippet = (start > 0 ? "..." : "") + plainText.substring(start, end) + (end < plainText.length() ? "..." : "");
            } else {
                snippet = narrative.getTitle();
            }

            results.add(new NarrativeSearchResultDTO(narrative.getId(), narrative.getTitle(), snippet));
        }

        return results;
    }

    private String extractTextFromDelta(String deltaJson) {
        if (deltaJson == null || deltaJson.isBlank()) return "";
        try {
            JsonNode root = objectMapper.readTree(deltaJson);
            JsonNode ops = root.get("ops");
            if (ops == null || !ops.isArray()) return "";
            StringBuilder text = new StringBuilder();
            for (JsonNode op : ops) {
                JsonNode insert = op.get("insert");
                if (insert != null && insert.isTextual()) {
                    text.append(insert.asText());
                }
            }
            return text.toString().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public NarrativeAssociationsResponseDTO updateAssociations(Long narrativeId,
                                                               NarrativeAssociationsRequestDTO request,
                                                               String email) {
        Narrative narrative = resolveNarrative(narrativeId, request.getProjectId(), email);

        List<Long> characterIds = request.getCharacterIds() != null ? request.getCharacterIds() : List.of();
        List<Long> wikiIds = request.getWikiIds() != null ? request.getWikiIds() : List.of();

        Project project = narrative.getProject();

        Set<StoryCharacter> characters = new HashSet<>();
        for (Long cid : characterIds) {
            StoryCharacter character = storyCharacterRepository.findByIdAndProject(cid, project)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Character " + cid + " not found in this project"));
            characters.add(character);
        }

        Set<Wiki> places = new HashSet<>();
        for (Long wid : wikiIds) {
            Wiki wiki = wikiRepository.findByIdAndProject(wid, project)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Wiki " + wid + " not found in this project"));
            places.add(wiki);
        }

        narrative.getCharacters().clear();
        narrative.getCharacters().addAll(characters);
        narrative.getPlaces().clear();
        narrative.getPlaces().addAll(places);

        repository.save(narrative);
        return toAssociationsDTO(narrative);
    }

    public NarrativeAssociationsResponseDTO getAssociations(Long narrativeId, Long projectId, String email) {
        Narrative narrative = resolveNarrative(narrativeId, projectId, email);
        return toAssociationsDTO(narrative);
    }

    private Narrative resolveNarrative(Long narrativeId, Long projectId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        if (!project.getUser().getEmail().equals(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access this project");
        }

        return repository.findByIdAndProject_Id(narrativeId, projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Narrative not found in this project"));
    }

    private NarrativeAssociationsResponseDTO toAssociationsDTO(Narrative narrative) {
        List<NarrativeCharacterDTO> characters = narrative.getCharacters().stream()
                .map(c -> new NarrativeCharacterDTO(c.getId(), c.getName(), c.getRole()))
                .toList();
        List<NarrativeWikiDTO> places = narrative.getPlaces().stream()
                .map(w -> new NarrativeWikiDTO(w.getId(), w.getTitle()))
                .toList();
        return new NarrativeAssociationsResponseDTO(characters, places);
    }

    private boolean isEffectivelyEmpty(String content) {
        if (content == null || content.isBlank()) return true;
        String c = content.strip().replaceAll("\\s+", "").replace("\\n", "");
        if (c.equals("{}")) return true;
        if (c.equals("{\"ops\":[]}")) return true;
        if (c.equals("{\"ops\":[{\"insert\":\"\"}]}")) return true;
        return false;
    }

    private NarrativeResponseDTO mapToDTO(Narrative c) {
        return new NarrativeResponseDTO(
                c.getId(),
                c.getTitle(),
                c.getContent(),
                c.getOrder()
        );
    }

    public NarrativeSuggestionResponseDTO getSuggestions(Long projectId,
                                                         NarrativeSuggestionRequestDTO dto,
                                                         String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        if(!project.getUser().getEmail().equals(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        List<Narrative> narratives = repository.findAllByProject_IdOrderByOrderAscIdAsc(projectId);

        if(narratives.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No narrative content found");
        }

        StringBuilder context = new StringBuilder();
        context.append("Here are all the chapters of the story:\n\n");
        for(Narrative n : narratives) {
            context.append("## Chapter: ").append(n.getTitle()).append("\n");
            context.append(extractTextFromDelta(n.getContent())).append("\n\n");
        }

        String suggestions = groqNarrativeSuggestionsService.getSuggestions(context.toString(), dto.getAdditionalInstructions());
        return new NarrativeSuggestionResponseDTO(suggestions);
    }
}