package com.yourname.blog.Blog.dto;

import java.time.LocalDateTime;

public record ArticleResponse(Long id, String title, String content, String excerpt, LocalDateTime createdAt,
                              String authorName, Long authorId, long likeCount, long commentCount, int readingMinutes,
                              boolean likedByCurrentUser, boolean bookmarked) { }
