package com.yourname.blog.Blog.controller;

import com.yourname.blog.Blog.dto.ArticleResponse;
import com.yourname.blog.Blog.dto.BlogRequest;
import com.yourname.blog.Blog.dto.CommentRequest;
import com.yourname.blog.Blog.dto.CommentResponse;
import com.yourname.blog.Blog.entity.Blog;
import com.yourname.blog.Blog.entity.Comment;
import com.yourname.blog.Blog.entity.User;
import com.yourname.blog.Blog.exception.ResourceNotFoundException;
import com.yourname.blog.Blog.repository.BlogRepository;
import com.yourname.blog.Blog.repository.CommentRepository;
import com.yourname.blog.Blog.repository.UserRepository;
import com.yourname.blog.Blog.service.ArticleMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import com.yourname.blog.Blog.entity.Follow;
import com.yourname.blog.Blog.repository.FollowRepository;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final BlogRepository blogs;
    private final UserRepository users;
    private final CommentRepository comments;
    private final ArticleMapper mapper;
    private final FollowRepository follows;

    private User me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return users.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public Page<ArticleResponse> feed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return blogs.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(mapper::map);
    }

    @GetMapping("/following")
    @Transactional(readOnly = true)
    public Page<ArticleResponse> following(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User current = me();
        List<User> followedUsers = follows.findByFollower(current)
                .stream()
                .map(Follow::getFollowed)
                .toList();

        if (followedUsers.isEmpty()) {
            return Page.empty(PageRequest.of(page, size));
        }

        return blogs.findByAuthorIn(followedUsers, PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(mapper::map);
    }

    @GetMapping("/mine")
    @Transactional(readOnly = true)
    public List<ArticleResponse> mine() {
        return blogs.findByAuthor(me())
                .stream()
                .map(mapper::map)
                .toList();
    }

    @GetMapping("/search")
    @Transactional(readOnly = true)
    public List<ArticleResponse> search(@RequestParam String q) {
        return blogs.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q)
                .stream()
                .map(mapper::map)
                .toList();
    }

    @GetMapping("/{id:\\d+}")
    @Transactional(readOnly = true)
    public ArticleResponse article(@PathVariable Long id) {
        Blog blog = blogs.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        return mapper.map(blog);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ArticleResponse> create(@Valid @RequestBody BlogRequest r) {
        Blog blog = Blog.builder()
                .title(r.getTitle())
                .content(r.getContent())
                .author(me())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(blogs.save(blog)));
    }

    @PutMapping("/{id}")
    @Transactional
    public ArticleResponse edit(@PathVariable Long id, @Valid @RequestBody BlogRequest r) {
        Blog blog = blogs.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        if (!blog.getAuthor().getId().equals(me().getId())) {
            throw new AccessDeniedException("You can only edit your own stories");
        }

        blog.setTitle(r.getTitle());
        blog.setContent(r.getContent());
        return mapper.map(blogs.save(blog));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Blog blog = blogs.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        if (!blog.getAuthor().getId().equals(me().getId())) {
            throw new AccessDeniedException("You can only delete your own stories");
        }

        blogs.delete(blog);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comments")
    @Transactional(readOnly = true)
    public List<CommentResponse> comments(@PathVariable Long id) {
        Blog blog = blogs.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        return comments.findByBlog(blog)
                .stream()
                .map(c -> new CommentResponse(c.getId(), c.getContent(), c.getUser().getName(), c.getCreatedAt()))
                .toList();
    }

    @PostMapping("/{id}/comments")
    @Transactional
    public CommentResponse comment(@PathVariable Long id, @RequestBody CommentRequest r) {
        Blog blog = blogs.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        Comment comment = comments.save(Comment.builder()
                .blog(blog)
                .user(me())
                .content(r.getContent())
                .createdAt(LocalDateTime.now())
                .build());

        return new CommentResponse(comment.getId(), comment.getContent(), comment.getUser().getName(), comment.getCreatedAt());
    }
}
