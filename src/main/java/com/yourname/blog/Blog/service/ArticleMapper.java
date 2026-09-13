package com.yourname.blog.Blog.service;

import com.yourname.blog.Blog.dto.ArticleResponse;
import com.yourname.blog.Blog.entity.Blog;
import com.yourname.blog.Blog.entity.User;
import com.yourname.blog.Blog.repository.BookmarkRepository;
import com.yourname.blog.Blog.repository.CommentRepository;
import com.yourname.blog.Blog.repository.LikeRepository;
import com.yourname.blog.Blog.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleMapper {

    private final LikeRepository likes;
    private final CommentRepository comments;
    private final BookmarkRepository bookmarks;
    private final UserRepository users;

    public ArticleResponse map(Blog blog) {
        String text = blog.getContent() != null ? blog.getContent() : "";
        String trimmed = text.trim();
        int wordCount = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
        int minutes = Math.max(1, (wordCount + 199) / 200);
        String excerpt = text.length() > 180 ? text.substring(0, 180) + "…" : text;

        boolean liked = false;
        boolean saved = false;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Optional<User> currentUser = users.findByEmail(auth.getName());
            if (currentUser.isPresent()) {
                liked = likes.findByBlogAndUser(blog, currentUser.get()).isPresent();
                saved = bookmarks.findByBlogAndUser(blog, currentUser.get()).isPresent();
            }
        }

        String authorName = blog.getAuthor() != null ? blog.getAuthor().getName() : "Anonymous";
        Long authorId = blog.getAuthor() != null ? blog.getAuthor().getId() : null;
        long likeCount = likes.countByBlog(blog);
        long commentCount = comments.countByBlog(blog);

        return new ArticleResponse(
                blog.getId(),
                blog.getTitle(),
                text,
                excerpt,
                blog.getCreatedAt(),
                authorName,
                authorId,
                likeCount,
                commentCount,
                minutes,
                liked,
                saved
        );
    }
}
