package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.StoryCharacter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoryCharacterRepository extends JpaRepository<StoryCharacter, Long> {

    /**
     * All characters for a project, ordered by creation date descending.
     * Simple derived query — safe with pagination.
     */
    Page<StoryCharacter> findByProjectOrderByCreatedAtDesc(Project project, Pageable pageable);

    /**
     * Characters matching a name search (case-insensitive) within a project.
     */
    Page<StoryCharacter> findByProjectAndNameContainingIgnoreCaseOrderByCreatedAtDesc(
            Project project, String name, Pageable pageable);

    /**
     * Finds a character by ID only if it belongs to the given project.
     * Returns empty if the character exists but belongs to a different project.
     */
    Optional<StoryCharacter> findByIdAndProject(Long id, Project project);
}