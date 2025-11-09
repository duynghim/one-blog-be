package com.onenotebe.mapper;

import com.onenotebe.dto.PostDetailDto;
import com.onenotebe.dto.PostSummaryDto;
import com.onenotebe.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "featuredImageUrl", source = "featuredImageUrl")
    PostSummaryDto toSummaryDto(Post post);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "featuredImageUrl", source = "featuredImageUrl")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    PostDetailDto toDetailDto(Post post);
}