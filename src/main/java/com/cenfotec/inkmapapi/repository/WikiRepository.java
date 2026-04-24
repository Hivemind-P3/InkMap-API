package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.Wiki;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WikiRepository extends JpaRepository<Wiki, Long> {

    Page<Wiki> findByProjectOrderByCreatedAtDesc(Project project, Pageable pageable);

    @Query("SELECT w FROM Wiki w WHERE w.project = :project AND LOWER(w.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY w.createdAt DESC")
    Page<Wiki> findByProjectAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            @Param("project") Project project, @Param("title") String title, Pageable pageable);

    Optional<Wiki> findByIdAndProject(Long id, Project project);
}
