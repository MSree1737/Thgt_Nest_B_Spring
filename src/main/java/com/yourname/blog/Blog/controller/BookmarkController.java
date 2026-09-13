package com.yourname.blog.Blog.controller;

import com.yourname.blog.Blog.dto.ArticleResponse;
import com.yourname.blog.Blog.entity.Blog;
import com.yourname.blog.Blog.entity.Bookmark;
import com.yourname.blog.Blog.entity.User;
import com.yourname.blog.Blog.exception.ResourceNotFoundException;
import com.yourname.blog.Blog.repository.BlogRepository;
import com.yourname.blog.Blog.repository.BookmarkRepository;
import com.yourname.blog.Blog.repository.UserRepository;
import com.yourname.blog.Blog.service.ArticleMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkRepository bookmarks;
    private final BlogRepository blogs;
    private final UserRepository users;
    private final ArticleMapper mapper;

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return users.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @PostMapping("/{blogId}")
    @Transactional
    public Map<String, Object> toggle(@PathVariable Long blogId) {
        Blog blog = blogs.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + blogId));

        User user = currentUser();
        Optional<Bookmark> existing = bookmarks.findByBlogAndUser(blog, user);

        if (existing.isPresent()) {
            bookmarks.delete(existing.get());
            bookmarks.flush();
            return Map.of("bookmarked", false, "message", "Story removed from saved");
        }

        bookmarks.save(Bookmark.builder().blog(blog).user(user).build());
        bookmarks.flush();
        return Map.of("bookmarked", true, "message", "Story saved to your library");
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<ArticleResponse> mine() {
        return bookmarks.findByUser(currentUser())
                .stream()
                .map(Bookmark::getBlog)
                .map(mapper::map)
                .toList();
    }
}
