package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.GeographicMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeographicMapRepository extends JpaRepository<GeographicMap, Long> {
    Page<GeographicMap> findAllByProjectId(Long id, Pageable pageable);
}