package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferencesRepository extends JpaRepository<Preferences, Long> {
    Optional<Preferences> findByUserId(Long id);
}
