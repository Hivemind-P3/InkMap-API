package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.Narrative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NarrativeRepository extends JpaRepository<Narrative, Long> {

    List<Narrative> findAllByProject_IdOrderByOrderAscIdAsc(Long projectId);

    Optional<Narrative> findByIdAndProject_Id(Long id, Long projectId);
}