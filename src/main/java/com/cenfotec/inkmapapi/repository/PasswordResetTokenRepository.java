package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.PasswordResetToken;
import com.cenfotec.inkmapapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
}