package com.yourname.blog.Blog.service;

import com.yourname.blog.Blog.dto.LikeResponse;

public interface LikeService {

    LikeResponse toggleLike(Long blogId);

    long getLikeCount(Long blogId);
}