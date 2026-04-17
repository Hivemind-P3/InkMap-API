package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.Wiki;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WikiRepository extends JpaRepository<Wiki, Long> {

    Page<Wiki> findByProjectOrderByCreatedAtDesc(Project project, Pageable pageable);

    Page<Wiki> findByProjectAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            Project project, String title, Pageable pageable);

    Optional<Wiki> findByIdAndProject(Long id, Project project);
}
