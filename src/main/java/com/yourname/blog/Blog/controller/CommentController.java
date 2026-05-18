package com.yourname.blog.Blog.controller;

import com.yourname.blog.Blog.dto.CommentRequest;
import com.yourname.blog.Blog.entity.Comment;
import com.yourname.blog.Blog.service.CommentService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public Comment addComment(@RequestBody CommentRequest request) {
        return commentService.addComment(request);
    }

    @GetMapping("/blog/{blogId}")
    public List<Comment> getComments(@PathVariable Long blogId) {
        return commentService.getCommentsByBlog(blogId);
    }
}
