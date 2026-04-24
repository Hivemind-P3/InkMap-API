package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.NodeMap;
import com.cenfotec.inkmapapi.models.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NodeMapRepository extends JpaRepository<NodeMap, Long> {

    /**
     * All node maps for a project, ordered by creation date descending.
     */
    Page<NodeMap> findByProjectOrderByCreatedAtDesc(Project project, Pageable pageable);

    /**
     * Node maps matching a name search (case-insensitive) within a project.
     */
    Page<NodeMap> findByProjectAndNameContainingIgnoreCaseOrderByCreatedAtDesc(
            Project project, String name, Pageable pageable);

    /**
     * Finds a node map by ID only if it belongs to the given project.
     */
    Optional<NodeMap> findByIdAndProject(Long id, Project project);
}
