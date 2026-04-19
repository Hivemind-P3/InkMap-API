package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.NarrativeVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NarrativeVersionRepository extends JpaRepository<NarrativeVersion, Long> {

    List<NarrativeVersion> findAllByNarrative_IdOrderByCreatedAtDesc(Long narrativeId);
}