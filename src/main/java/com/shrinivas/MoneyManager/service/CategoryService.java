package com.shrinivas.MoneyManager.service;

import com.shrinivas.MoneyManager.dto.CategoryDto;
import com.shrinivas.MoneyManager.entity.CategoryEntity;
import com.shrinivas.MoneyManager.entity.ProfileEntity;
import com.shrinivas.MoneyManager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CategoryService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;
    private String existingCategory;

    //save Category
    public CategoryDto saveCategory(CategoryDto categoryDto){
        ProfileEntity profile=profileService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryDto.getName(),profile.getId())){
            throw new RuntimeException("ategory with this name already exist");

        }
        CategoryEntity newcategory=toEntity(categoryDto,profile);
        newcategory=categoryRepository.save(newcategory);
        return toDto(newcategory);
    }
    //get category of current user
    public List<CategoryDto> getCategoriesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity> categories=categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDto).toList();
    }
    //get category of by type for current user
    public List<CategoryDto> getCategoriesByTypeCurrentUser(String type){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity> entities=categoryRepository.findByTypeAndProfileId(type,profile.getId());
        return entities.stream().map(this::toDto).toList();
    }
    public CategoryDto updateCategory(Long categoryId,CategoryDto dto){
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity existingCategory=categoryRepository.findByIdAndProfileId(categoryId,profile.getId())
                .orElseThrow(()->new RuntimeException("Category not found or not accessible"));
        existingCategory.setName(dto.getName());
        existingCategory.setIcon(dto.getIcon());
        existingCategory.setType(dto.getType());
        categoryRepository.save(existingCategory);
        return toDto(existingCategory);

    }

    private CategoryEntity toEntity(CategoryDto categoryDto, ProfileEntity profile){
        return CategoryEntity.builder()
                .name(categoryDto.getName())
                .icon(categoryDto.getName())
                .profile(profile)
                .type(categoryDto.getType())
                .build();
    }
    private CategoryDto toDto(CategoryEntity entity){
        return CategoryDto.builder()
                .id(entity.getId())
                .profileId(entity.getProfile()!=null ? entity.getProfile().getId():null)
                .name(entity.getName())
                .icon(entity.getIcon())
                .updatedAt(entity.getUpdatedAt())
                .type(entity.getType())
                .build();

    }
}
