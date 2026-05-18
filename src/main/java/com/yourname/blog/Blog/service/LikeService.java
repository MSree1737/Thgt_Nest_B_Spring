package com.yourname.blog.Blog.service;

public interface LikeService {

    String toggleLike(Long blogId);

    long getLikeCount(Long blogId);
}