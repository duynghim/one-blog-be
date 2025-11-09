package com.onenotebe.service;

import com.onenotebe.dto.CategoryDto;
import com.onenotebe.dto.CreateCategoryDto;
import com.onenotebe.dto.UpdateCategoryDto;
import java.util.List;

/**
 * Service interface for Category management.
 */
public interface CategoryService {

    /** Create a new category from the given request. */
    CategoryDto create(CreateCategoryDto dto);

    /** List categories using page and size for pagination. */
    List<CategoryDto> findAll(int page, int size);

    /** Find a category by its ID or throw ResourceNotFoundException. */
    CategoryDto findById(Long id);

    /** Find a category by its slug or throw ResourceNotFoundException. */
    CategoryDto findBySlug(String slug);

    /** Update the name (and regenerate slug) of a category. */
    CategoryDto update(Long id, UpdateCategoryDto dto);

    /** Delete a category by its ID. */
    void delete(Long id);
}