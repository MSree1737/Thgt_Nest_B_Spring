package com.yourname.blog.Blog.service;

import com.yourname.blog.Blog.dto.CommentRequest;
import com.yourname.blog.Blog.entity.*;
import com.yourname.blog.Blog.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    @Override
    public Comment addComment(CommentRequest request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Blog blog = blogRepository.findById(request.getBlogId())
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .blog(blog)
                .user(user)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByBlog(Long blogId) {

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        return commentRepository.findByBlog(blog);
    }
}
