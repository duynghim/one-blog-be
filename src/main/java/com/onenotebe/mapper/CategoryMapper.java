package com.onenotebe.mapper;

import com.onenotebe.dto.CategoryDto;
import com.onenotebe.dto.CreateCategoryDto;
import com.onenotebe.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Category entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {

    /**
     * Convert Category entity to CategoryDto.
     */
    CategoryDto toDto(Category category);

    /**
     * Convert CreateCategoryDto to Category entity. Slug is generated from name.
     */
    @Mapping(target = "slug", expression = "java(slugify(dto.name()))")
    Category toEntity(CreateCategoryDto dto);

    /**
     * Generate a URL-friendly slug from a category name.
     * Uses lowercase and replaces whitespace with hyphens.
     */
    default String slugify(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }
        var normalized = name.trim().toLowerCase();
        // replace all non-alphanumeric sequences with a single hyphen
        normalized = normalized.replaceAll("[^a-z0-9]+", "-");
        // trim hyphens from start/end
        normalized = normalized.replaceAll("(^-+)|(-+$)", "");
        return normalized;
    }
}