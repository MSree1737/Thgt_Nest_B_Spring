package com.yourname.blog.Blog.controller;

import com.yourname.blog.Blog.service.LikeService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{blogId}")
    public String toggleLike(@PathVariable Long blogId) {
        return likeService.toggleLike(blogId);
    }

    @GetMapping("/{blogId}")
    public long getLikes(@PathVariable Long blogId) {
        return likeService.getLikeCount(blogId);
    }
}