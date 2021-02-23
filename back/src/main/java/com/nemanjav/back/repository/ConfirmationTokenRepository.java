package com.nemanjav.back.repository;

import com.nemanjav.back.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken , Long> {

    Optional<ConfirmationToken> findByToken(String token);
}
