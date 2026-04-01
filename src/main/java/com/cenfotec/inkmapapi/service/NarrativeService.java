package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.*;
import com.cenfotec.inkmapapi.models.*;
import com.cenfotec.inkmapapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NarrativeService {

    private final NarrativeRepository repository;
    private final ProjectRepository projectRepository;

    public NarrativeResponseDTO create(CreateNarrativeDTO dto, Long userId) {

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Narrative narrative = new Narrative();
        narrative.setTitle(dto.getTitle());
        narrative.setContent("{}"); // vacío tipo Delta
        narrative.setProject(project);
        narrative.setCreationTime(LocalDateTime.now());
        narrative.setUpdateTime(LocalDateTime.now());

        int order = repository.findAllByProject_IdOrderByIdAsc(dto.getProjectId()).size();
        narrative.setOrder(order);

        repository.save(narrative);

        return mapToDTO(narrative);
    }

    public NarrativeResponseDTO edit(Long narrativeId, UpdateNarrativeDTO dto, Long userId) {

        Narrative narrative = repository
                .findByIdAndProject_Id(narrativeId, dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Narrative content not found"));

        if (dto.getTitle() != null) {
            if (dto.getTitle().length() < 2 || dto.getTitle().length() > 120) {
                throw new RuntimeException("Invalid title");
            }
            narrative.setTitle(dto.getTitle());
        }

        if (dto.getContent() == null || dto.getContent().isEmpty()) {
            throw new RuntimeException("Narrative content cannot be empty");
        }

        narrative.setContent(dto.getContent());
        narrative.setUpdateTime(LocalDateTime.now());

        repository.save(narrative);

        return mapToDTO(narrative);
    }

    public List<NarrativeResponseDTO> listByProject(Long projectId) {

        return repository.findAllByProject_IdOrderByIdAsc(projectId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<NarrativeResponseDTO> reorder(NarrativeOrderDTO dto, Long userId) {

        List<Narrative> narratives =
                repository.findAllByProject_IdOrderByIdAsc(dto.getProjectId());

        Map<Long, Narrative> map = narratives.stream()
                .collect(Collectors.toMap(Narrative::getId, c -> c));

        for (int i = 0; i < dto.getOrderedIds().size(); i++) {
            Long id = dto.getOrderedIds().get(i);

            if (!map.containsKey(id)) {
                throw new RuntimeException("Invalid chapter");
            }

            Narrative c = map.get(id);
            c.setOrder(i);
        }

        repository.saveAll(narratives);

        return narratives.stream().map(this::mapToDTO).toList();
    }

    private NarrativeResponseDTO mapToDTO(Narrative c) {
        return new NarrativeResponseDTO(
                c.getId(),
                c.getTitle(),
                c.getContent(),
                c.getOrder()
        );
    }
}