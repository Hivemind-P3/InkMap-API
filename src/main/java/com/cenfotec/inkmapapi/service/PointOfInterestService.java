package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.CreatePointOfInterestRequestDTO;
import com.cenfotec.inkmapapi.dto.PointOfInterestResponseDTO;
import com.cenfotec.inkmapapi.models.GeographicMap;
import com.cenfotec.inkmapapi.models.PointOfInterest;
import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.repository.GeographicMapRepository;
import com.cenfotec.inkmapapi.repository.PointOfInterestRepository;
import com.cenfotec.inkmapapi.repository.ProjectRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointOfInterestService {

    private final PointOfInterestRepository pointOfInterestRepository;
    private final GeographicMapRepository geographicMapRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public PointOfInterestResponseDTO createPointOfInterest(String email, Long projectId, Long mapId, CreatePointOfInterestRequestDTO request) {
        GeographicMap map = resolveMap(email, projectId, mapId);

        PointOfInterest poi = new PointOfInterest();
        poi.setPosX(request.getPosX());
        poi.setPosY(request.getPosY());
        poi.setGeographicMap(map);
        pointOfInterestRepository.save(poi);

        return toDTO(poi);
    }

    public List<PointOfInterestResponseDTO> getPointsOfInterest(String email, Long projectId, Long mapId) {
        resolveMap(email, projectId, mapId);
        return pointOfInterestRepository.findAllByGeographicMapId(mapId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void deletePointOfInterest(String email, Long projectId, Long mapId, Long poiId) {
        resolveMap(email, projectId, mapId);
        PointOfInterest poi = pointOfInterestRepository.findById(poiId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Point of interest not found"));
        if (!poi.getGeographicMap().getId().equals(mapId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Point of interest does not belong to this map");
        }
        pointOfInterestRepository.delete(poi);
    }

    private GeographicMap resolveMap(String email, Long projectId, Long mapId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        if (!project.getUser().getEmail().equals(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access this project");
        }

        GeographicMap map = geographicMapRepository.findById(mapId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Geographic map not found"));

        if (!map.getProject().getId().equals(projectId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Geographic map does not belong to this project");
        }

        return map;
    }

    private PointOfInterestResponseDTO toDTO(PointOfInterest poi) {
        return new PointOfInterestResponseDTO(
                poi.getId(),
                poi.getPosX(),
                poi.getPosY(),
                poi.getGeographicMap().getId(),
                poi.getCreatedAt(),
                poi.getUpdatedAt()
        );
    }
}
