package com.onenotebe.service;

import com.onenotebe.dto.PostDetailDto;
import com.onenotebe.dto.PostSummaryDto;
import com.onenotebe.exception.ResourceNotFoundException;
import com.onenotebe.mapper.PostMapper;
import com.onenotebe.model.Post;
import com.onenotebe.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

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
                .orElseThrow(() -> new ResourceNotFoundException("Post not found for id: " + id));
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
}