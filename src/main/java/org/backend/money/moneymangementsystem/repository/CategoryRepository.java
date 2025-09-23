package org.backend.money.moneymangementsystem.repository;

import org.backend.money.moneymangementsystem.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

        List<Category> findByProfileId(Long profileId);

        Optional<Category> findByIdAndProfileId(Long id, Long profileId);

        List<Category>findByTypeAndProfileId(String type, Long profileId);

        Boolean existsByNameAndProfileId(String name,Long ProfileId);
}
