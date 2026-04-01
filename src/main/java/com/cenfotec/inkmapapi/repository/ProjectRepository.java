package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * All projects for a user, ordered by creation date descending.
     * Simple derived query — no join on tags, safe with pagination.
     */
    Page<Project> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * Projects matching a search term against title or tags (case-insensitive).
     * Uses EXISTS subquery for tags to avoid DISTINCT + collection-join + pagination issues
     * that occur when using LEFT JOIN on @ElementCollection with Hibernate 6.
     */
    @Query(value = """
            SELECT p FROM Project p
            WHERE p.user = :user
              AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR EXISTS (
                       SELECT ip FROM Project ip JOIN ip.tags t
                       WHERE ip = p AND LOWER(t) LIKE LOWER(CONCAT('%', :search, '%'))
                   ))
            """,
           countQuery = """
            SELECT COUNT(p) FROM Project p
            WHERE p.user = :user
              AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR EXISTS (
                       SELECT ip FROM Project ip JOIN ip.tags t
                       WHERE ip = p AND LOWER(t) LIKE LOWER(CONCAT('%', :search, '%'))
                   ))
            """)
    Page<Project> findByUserWithSearch(@Param("user") User user,
                                       @Param("search") String search,
                                       Pageable pageable);

    Optional<Project> findByUserAndTitle(User user, String title);

    /**
     * Finds a project by user and title, excluding a specific project ID.
     * Used during updates to allow keeping the same title while preventing
     * duplicate titles with other projects owned by the same user.
     */
    Optional<Project> findByUserAndTitleAndIdNot(User user, String title, Long id);
}
