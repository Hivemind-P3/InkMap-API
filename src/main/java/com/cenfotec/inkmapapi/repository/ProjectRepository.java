package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.Project;
import com.cenfotec.inkmapapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByUserOrderByCreatedAtDesc(User user);

    Optional<Project> findByUserAndTitle(User user, String title);
}
