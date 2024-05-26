package com.example.applifting.repositories;

import com.example.applifting.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    boolean existsAppUserByEmail(String email);

    Optional<AppUser> findAppUserByAccessToken(String accessToken);

    Optional<AppUser> findAppUserByEmail(String accessToken);
}
