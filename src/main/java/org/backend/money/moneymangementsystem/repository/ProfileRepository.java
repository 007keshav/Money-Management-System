package org.backend.money.moneymangementsystem.repository;

import org.backend.money.moneymangementsystem.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile,Long> {
    Optional<Profile> findByEmail(String email);
    Optional<Profile> findByActivationToken(String activationToken);
}
