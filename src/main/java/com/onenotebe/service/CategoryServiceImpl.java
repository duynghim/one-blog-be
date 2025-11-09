package com.onenotebe.service;

import com.onenotebe.dto.CategoryDto;
import com.onenotebe.dto.CreateCategoryDto;
import com.onenotebe.dto.UpdateCategoryDto;
import com.onenotebe.exception.ResourceNotFoundException;
import com.onenotebe.mapper.CategoryMapper;
import com.onenotebe.model.Category;
import com.onenotebe.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of CategoryService with transactional CRUD operations.
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private static final String CATEGORY_NOT_FOUND = "Category not found for id= ";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryDto create(CreateCategoryDto dto) {
        log.info("Creating category [name={}]", dto.name());
        Category category = categoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        log.info("Category created [id={}, slug={}]", saved.getId(), saved.getSlug());
        return categoryMapper.toDto(saved);
    }

    @Override
    public List<CategoryDto> findAll(int page, int size) {
        log.debug("Listing categories [page={}, size={}]", page, size);
        var pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        return categoryRepository.findAll(pageable)
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long id) {
        log.debug("Finding category by id [id={}]", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND + id));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto findBySlug(String slug) {
        log.debug("Finding category by slug [slug={}]", slug);
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND + slug));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, UpdateCategoryDto dto) {
        log.info("Updating category [id={}, name={}]", id, dto.name());
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND + id));

        var newName = dto.name();
        if (newName != null && !newName.isBlank() && !newName.equals(category.getName())) {
            category.setName(newName);
            // regenerate slug using same logic as mapper.
            var newSlug = categoryMapper.slugify(newName);
            category.setSlug(newSlug);
            log.debug("Regenerated slug for category [id={}, slug={}]", id, newSlug);
        }

        Category updated = categoryRepository.save(category);
        log.info("Category updated [id={}, slug={}]", updated.getId(), updated.getSlug());
        return categoryMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting category [id={}]", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND + id));
        categoryRepository.delete(category);
        log.info("Category deleted [id={}]", id);
    }
}