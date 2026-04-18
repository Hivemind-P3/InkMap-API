package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Long> {
    List<PointOfInterest> findAllByGeographicMapId(Long mapId);
}