package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.NarrativeResponseDTO;
import com.cenfotec.inkmapapi.dto.NarrativeVersionResponseDTO;
import com.cenfotec.inkmapapi.models.Narrative;
import com.cenfotec.inkmapapi.models.NarrativeVersion;
import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.NarrativeRepository;
import com.cenfotec.inkmapapi.repository.NarrativeVersionRepository;
import com.cenfotec.inkmapapi.repository.ProjectRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class NarrativeVersionService {

    private final NarrativeVersionRepository versionRepository;
    private final NarrativeRepository narrativeRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public NarrativeVersionResponseDTO createVersion(String email, Long projectId, Long narrativeId) {
        resolveProject(email, projectId);

        Narrative narrative = narrativeRepository.findByIdAndProject_Id(narrativeId, projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Narrative not found"));

        if (isEffectivelyEmpty(narrative.getContent())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Narrative content cannot be empty");
        }

        NarrativeVersion version = new NarrativeVersion();
        version.setContent(narrative.getContent());
        version.setNarrative(narrative);

        return toDTO(versionRepository.save(version));
    }

    public List<NarrativeVersionResponseDTO> listVersions(String email, Long projectId, Long narrativeId) {
        resolveProject(email, projectId);

        narrativeRepository.findByIdAndProject_Id(narrativeId, projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Narrative not found"));

        return versionRepository.findAllByNarrative_IdOrderByCreatedAtDesc(narrativeId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public NarrativeResponseDTO restoreVersion(String email, Long projectId, Long narrativeId, Long versionId) {
        resolveProject(email, projectId);

        Narrative narrative = narrativeRepository.findByIdAndProject_Id(narrativeId, projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Narrative not found"));

        NarrativeVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Version not found"));

        if (!version.getNarrative().getId().equals(narrativeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Version does not belong to this narrative");
        }

        narrative.setContent(version.getContent());
        narrative.setUpdateTime(LocalDateTime.now());

        Narrative saved = narrativeRepository.save(narrative);
        return new NarrativeResponseDTO(saved.getId(), saved.getTitle(), saved.getContent(), saved.getOrder());
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

    private boolean isEffectivelyEmpty(String content) {
        if (content == null || content.isBlank()) return true;
        // replaceAll removes actual newline chars; replace removes the JSON-escaped \n sequence
        String c = content.strip().replaceAll("\\s+", "").replace("\\n", "");
        if (c.equals("{}")) return true;
        if (c.equals("{\"ops\":[]}")) return true;
        if (c.equals("{\"ops\":[{\"insert\":\"\"}]}")) return true;
        return false;
    }

    private NarrativeVersionResponseDTO toDTO(NarrativeVersion v) {
        return new NarrativeVersionResponseDTO(
                v.getId(),
                v.getNarrative().getId(),
                v.getContent(),
                v.getCreatedAt()
        );
    }
}