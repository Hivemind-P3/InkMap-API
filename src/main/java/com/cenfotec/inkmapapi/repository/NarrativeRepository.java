package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.Narrative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NarrativeRepository extends JpaRepository<Narrative, Long> {

    List<Narrative> findAllByProject_IdOrderByOrderAscIdAsc(Long projectId);

    Optional<Narrative> findByIdAndProject_Id(Long id, Long projectId);

    @Query("SELECT n.title FROM Narrative n WHERE n.project.id = :projectId ORDER BY n.order ASC, n.id ASC")
    List<String> findTitlesByProjectIdOrdered(@Param("projectId") Long projectId);
}