package com.cenfotec.inkmapapi.service;

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

        return ResponseEntity.ok(geographicMap);
    }

    public ResponseEntity<?> getAllGeographicalMapsByProjectId(int page, int size, Long projectId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GeographicMap> geographicMaps = geographicMapRepository.findAllByProjectId(projectId, pageable);

        return ResponseEntity.ok(geographicMaps);
    }

    public ResponseEntity<?> createGeographicMap(Long projectId, GeographicMap geographicMap) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project with id " + projectId + " not found");
        }

        GeographicMap newGeographicMap = new GeographicMap();
        newGeographicMap.setName(geographicMap.getName());
        newGeographicMap.setKonvaJson(geographicMap.getKonvaJson());
        newGeographicMap.setProject(geographicMap.getProject());

        return ResponseEntity.ok(geographicMapRepository.save(newGeographicMap));
    }

    public ResponseEntity<?> deleteGeographicMap(Long id) {
        geographicMapRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Geographic map with id " + id + " not found"));

        geographicMapRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
