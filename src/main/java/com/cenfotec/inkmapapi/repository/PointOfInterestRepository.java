package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.GeographicMap;
import com.cenfotec.inkmapapi.models.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Long> {
    List<PointOfInterest> findAllByGeographicMapId(Long mapId);
    Optional<PointOfInterest> findByIdAndGeographicMap(Long id, GeographicMap geographicMap);
}