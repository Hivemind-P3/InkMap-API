package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.StoryCharacter;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.models.Wiki;
import com.cenfotec.inkmapapi.repository.NarrativeRepository;
import com.cenfotec.inkmapapi.repository.ProjectRepository;
import com.cenfotec.inkmapapi.repository.StoryCharacterRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import com.cenfotec.inkmapapi.repository.WikiRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProjectContextService {

    private static final int MAX_CHARACTERS = 5;
    private static final int MAX_WIKIS = 3;
    private static final int MAX_NARRATIVES = 3;
    private static final int WIKI_CONTENT_LIMIT = 300;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final StoryCharacterRepository characterRepository;
    private final WikiRepository wikiRepository;
    private final NarrativeRepository narrativeRepository;

    public ProjectContextService(UserRepository userRepository,
                                  ProjectRepository projectRepository,
                                  StoryCharacterRepository characterRepository,
                                  WikiRepository wikiRepository,
                                  NarrativeRepository narrativeRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.characterRepository = characterRepository;
        this.wikiRepository = wikiRepository;
        this.narrativeRepository = narrativeRepository;
    }

    public String buildContext(String email, Long projectId) {
        Project project = resolveProject(email, projectId);

        StringBuilder sb = new StringBuilder();

        appendProjectInfo(sb, project);
        appendCharacters(sb, project);
        appendWikis(sb, project);
        appendNarratives(sb, project.getId());

        return sb.toString().trim();
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

    private void appendProjectInfo(StringBuilder sb, Project project) {
        sb.append("PROYECTO: ").append(project.getTitle()).append("\n");
        if (project.getMedium() != null && !project.getMedium().isBlank()) {
            sb.append("MEDIO: ").append(project.getMedium()).append("\n");
        }
        if (project.getDescription() != null && !project.getDescription().isBlank()) {
            sb.append("DESCRIPCIÓN: ").append(project.getDescription()).append("\n");
        }
        if (project.getTags() != null && !project.getTags().isEmpty()) {
            sb.append("ETIQUETAS: ").append(String.join(", ", project.getTags())).append("\n");
        }
        sb.append("\n");
    }

    private void appendCharacters(StringBuilder sb, Project project) {
        List<StoryCharacter> characters = characterRepository
                .findByProjectOrderByCreatedAtDesc(project, PageRequest.of(0, MAX_CHARACTERS))
                .getContent();

        if (characters.isEmpty()) return;

        sb.append("PERSONAJES EXISTENTES:\n");
        for (StoryCharacter c : characters) {
            sb.append("- ").append(c.getName());
            if (c.getRole() != null && !c.getRole().isBlank()) {
                sb.append(" (").append(c.getRole()).append(")");
            }
            if (c.getDescription() != null && !c.getDescription().isBlank()) {
                sb.append(": ").append(c.getDescription());
            }
            sb.append("\n");
        }
        sb.append("\n");
    }

    private void appendWikis(StringBuilder sb, Project project) {
        List<Wiki> wikis = wikiRepository
                .findByProjectOrderByCreatedAtDesc(project, PageRequest.of(0, MAX_WIKIS))
                .getContent();

        if (wikis.isEmpty()) return;

        sb.append("LORE DEL MUNDO:\n");
        for (Wiki w : wikis) {
            sb.append("- ").append(w.getTitle());
            if (w.getContent() != null && !w.getContent().isBlank()) {
                String content = truncate(w.getContent(), WIKI_CONTENT_LIMIT);
                sb.append(": ").append(content);
            }
            sb.append("\n");
        }
        sb.append("\n");
    }

    private void appendNarratives(StringBuilder sb, Long projectId) {
        List<String> titles = narrativeRepository
                .findTitlesByProjectIdOrdered(projectId)
                .stream()
                .limit(MAX_NARRATIVES)
                .toList();

        if (titles.isEmpty()) return;

        sb.append("CAPÍTULOS:\n");
        for (String title : titles) {
            sb.append("- ").append(title).append("\n");
        }
        sb.append("\n");
    }

    private String truncate(String text, int limit) {
        return text.length() > limit ? text.substring(0, limit) + "..." : text;
    }
}
