package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.WikiResponseDTO;
import com.cenfotec.inkmapapi.models.GeographicMap;
import com.cenfotec.inkmapapi.models.PointOfInterest;
import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.models.Wiki;
import com.cenfotec.inkmapapi.repository.GeographicMapRepository;
import com.cenfotec.inkmapapi.repository.PointOfInterestRepository;
import com.cenfotec.inkmapapi.repository.ProjectRepository;
import com.cenfotec.inkmapapi.repository.UserRepository;
import com.cenfotec.inkmapapi.repository.WikiRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PointOfInterestWikiService {

    private final PointOfInterestRepository poiRepository;
    private final GeographicMapRepository geographicMapRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final WikiRepository wikiRepository;

    public PointOfInterestWikiService(PointOfInterestRepository poiRepository,
                                      GeographicMapRepository geographicMapRepository,
                                      ProjectRepository projectRepository,
                                      UserRepository userRepository,
                                      WikiRepository wikiRepository) {
        this.poiRepository = poiRepository;
        this.geographicMapRepository = geographicMapRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.wikiRepository = wikiRepository;
    }

    @Transactional(readOnly = true)
    public List<WikiResponseDTO> getWikisForPoi(String email, Long projectId, Long mapId, Long poiId) {
        PointOfInterest poi = resolvePoi(email, projectId, mapId, poiId);
        return poi.getWikis().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public void addWikiToPoi(String email, Long projectId, Long mapId, Long poiId, Long wikiId) {
        Project project = resolveProject(email, projectId);

        GeographicMap map = geographicMapRepository.findByIdAndProject(mapId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Geographic map not found"));

        PointOfInterest poi = poiRepository.findByIdAndGeographicMap(poiId, map)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Point of interest not found"));

        Wiki wiki = wikiRepository.findByIdAndProject(wikiId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Wiki not found or does not belong to this project"));

        boolean alreadyLinked = poi.getWikis().stream()
                .anyMatch(w -> w.getId().equals(wiki.getId()));
        if (alreadyLinked) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Wiki is already associated with this point of interest");
        }

        poi.getWikis().add(wiki);
        poiRepository.save(poi);
    }

    @Transactional
    public void removeWikiFromPoi(String email, Long projectId, Long mapId, Long poiId, Long wikiId) {
        Project project = resolveProject(email, projectId);

        GeographicMap map = geographicMapRepository.findByIdAndProject(mapId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Geographic map not found"));

        PointOfInterest poi = poiRepository.findByIdAndGeographicMap(poiId, map)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Point of interest not found"));

        boolean removed = poi.getWikis().removeIf(w -> w.getId().equals(wikiId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wiki is not associated with this point of interest");
        }

        poiRepository.save(poi);
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

    private PointOfInterest resolvePoi(String email, Long projectId, Long mapId, Long poiId) {
        Project project = resolveProject(email, projectId);

        GeographicMap map = geographicMapRepository.findByIdAndProject(mapId, project)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Geographic map not found"));

        return poiRepository.findByIdAndGeographicMap(poiId, map)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Point of interest not found"));
    }

    private WikiResponseDTO toDTO(Wiki w) {
        return new WikiResponseDTO(
                w.getId(),
                w.getTitle(),
                w.getContent(),
                w.getProject().getId(),
                w.getCreatedAt(),
                w.getUpdatedAt()
        );
    }
}