package com.yourname.blog.Blog.dto;

public record FollowResponse(boolean following, long followersCount, long followingCount, String message) { }
