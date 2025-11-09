package com.onenotebe.service;

import com.onenotebe.dto.CreatePostDto;
import com.onenotebe.dto.PostDetailDto;
import com.onenotebe.dto.PostSummaryDto;
import com.onenotebe.exception.ResourceNotFoundException;
import com.onenotebe.mapper.PostMapper;
import com.onenotebe.model.Post;
import com.onenotebe.model.Category;
import com.onenotebe.repository.PostRepository;
import com.onenotebe.repository.CategoryRepository;
import com.onenotebe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private static final String POST_NOT_FOUND = "Post not found for id: ";

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Cacheable(cacheNames = "postsBySlug", key = "#slug")
    public PostDetailDto getBySlug(String slug) {
        log.debug("Retrieving post by slug [{}]", slug);
        var post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found for slug: " + slug));
        log.info("Post retrieved by slug [slug={}, id={}]", slug, post.getId());
        return postMapper.toDetailDto(post);
    }

    @Cacheable(cacheNames = "postsById", key = "#id")
    public PostDetailDto getById(Long id) {
        log.debug("Retrieving post by id [{}]", id);
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + id));
        log.info("Post retrieved by id [id={}, slug={}]", id, post.getSlug());
        return postMapper.toDetailDto(post);
    }

    public List<PostSummaryDto> listAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 20 : Math.min(size, 100));
        log.debug("Listing posts [page={}, size={}]", pageable.getPageNumber(), pageable.getPageSize());
        Page<Post> posts = postRepository.findAll(pageable);
        var result = posts.map(postMapper::toSummaryDto).getContent();
        log.info("Listed posts [count={}]", result.size());
        return result;
    }

    public PostDetailDto create(CreatePostDto dto, String authorUsername) {
        log.info("Creating post [title={}]", dto.title());
        var author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + authorUsername));
        var categories = resolveCategories(dto.categoryIds());
        var post = Post.builder()
                .title(dto.title())
                .slug(slugify(dto.title()))
                .content(dto.content())
                .featuredImageUrl(dto.featuredImageUrl())
                .author(author)
                .categories(categories)
                .build();
        var saved = postRepository.save(post);
        log.info("Post created [id={}, slug={}]", saved.getId(), saved.getSlug());
        return postMapper.toDetailDto(saved);
    }

    public PostDetailDto update(Long id, CreatePostDto dto) {
        log.info("Updating post [id={}, title={}]", id, dto.title());
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + id));
        post.setTitle(dto.title());
        post.setSlug(slugify(dto.title()));
        post.setContent(dto.content());
        post.setFeaturedImageUrl(dto.featuredImageUrl());
        post.setCategories(resolveCategories(dto.categoryIds()));
        var saved = postRepository.save(post);
        log.info("Post updated [id={}, slug={}]", saved.getId(), saved.getSlug());
        return postMapper.toDetailDto(saved);
    }

    public void delete(Long id) {
        log.info("Deleting post [id={}]", id);
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + id));
        postRepository.delete(post);
        log.info("Post deleted [id={}]", id);
    }

    private Set<Category> resolveCategories(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Set.of();
        }
        var provided = Set.copyOf(categoryIds);
        var categories = categoryRepository.findByIdIn(provided);
        var foundIds = categories.stream().map(Category::getId).collect(Collectors.toSet());
        if (!foundIds.containsAll(provided) || foundIds.size() != provided.size()) {
            var missing = provided.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toSet());
            throw new ResourceNotFoundException("Invalid category IDs: " + missing);
        }
        return categories;
    }

    private String slugify(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        return input.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("-+", "-")
                .replaceAll("(^-)|-($)", "");
    }
}