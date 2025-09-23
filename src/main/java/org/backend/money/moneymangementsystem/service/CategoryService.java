package org.backend.money.moneymangementsystem.service;


import lombok.RequiredArgsConstructor;
import org.backend.money.moneymangementsystem.dto.CategoryDTO;
import org.backend.money.moneymangementsystem.entity.Category;
import org.backend.money.moneymangementsystem.entity.Profile;
import org.backend.money.moneymangementsystem.repository.CategoryRepository;
import org.backend.money.moneymangementsystem.repository.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
    public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    //save category

    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        Profile profile = profileService.getCurrentProfile();

        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category with this name already exists");
        }

        // Convert DTO to Entity and save
        Category newCategory = toEntity(categoryDTO, profile);
        newCategory = categoryRepository.save(newCategory);

        return toDTO(newCategory);
    }




    //helper methods

    private Category toEntity(CategoryDTO categoryDTO, Profile profile) {
        return Category.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    };

    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .profileId(category.getProfile() != null ? category.getProfile().getId():null)
                .name(category.getName())
                .icon(category.getIcon())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .type(category.getType())
                .build();
    }


    //get categories for  current user

}
