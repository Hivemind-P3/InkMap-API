package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.GeographicMapDTO;
import com.cenfotec.inkmapapi.dto.GeographicMapSaveDTO;
import com.cenfotec.inkmapapi.models.GeographicMap;
import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.repository.GeographicMapRepository;
import com.cenfotec.inkmapapi.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Servicio encargado de manejar la lógica de gestión de mapas geográficos
 */
@Service
@RequiredArgsConstructor
public class GeographicMapService {

    private final GeographicMapRepository geographicMapRepository;
    private final ProjectRepository projectRepository;

    public ResponseEntity<?> getGeographicalMapById(Long id) {
        GeographicMap geographicMap = geographicMapRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Geographic map with id " + id + " not found"));

        GeographicMapDTO dto = new GeographicMapDTO();
        dto.setId(geographicMap.getId());
        dto.setName(geographicMap.getName());
        dto.setKonvaJson(geographicMap.getKonvaJson());
        dto.setThumbnail(geographicMap.getThumbnail());
        dto.setProjectId(geographicMap.getProject().getId());
        dto.setCreatedAt(geographicMap.getCreatedAt());
        dto.setUpdatedAt(geographicMap.getUpdatedAt());

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<?> getAllGeographicalMapsByProjectId(int page, int size, Long projectId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GeographicMap> geographicMaps = geographicMapRepository.findAllByProjectId(projectId, pageable);

        Page<GeographicMapDTO> dtos = geographicMaps.map(map -> {
            GeographicMapDTO dto = new GeographicMapDTO();
            dto.setId(map.getId());
            dto.setName(map.getName());
            dto.setProjectId(map.getProject().getId());
            dto.setThumbnail(map.getThumbnail());
            dto.setCreatedAt(map.getCreatedAt());
            dto.setUpdatedAt(map.getUpdatedAt());
            return dto;
        });

        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<?> saveMap(Long id, GeographicMapSaveDTO dto) {
        GeographicMap map = geographicMapRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Map not found"));
        map.setKonvaJson(dto.getKonvaJson());
        map.setName(dto.getName());
        map.setThumbnail(dto.getThumbnail());
        geographicMapRepository.save(map);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> createMap(Long projectId, GeographicMapSaveDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        GeographicMap map = new GeographicMap();
        map.setName(dto.getName());
        map.setProject(project);
        map.setKonvaJson("");
        geographicMapRepository.save(map);

        return ResponseEntity.ok(map.getId());
    }
}
