package com.yourname.blog.Blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlogResponse {

    private Long id;
    private String title;
    private String content;
    private String authorName;
}