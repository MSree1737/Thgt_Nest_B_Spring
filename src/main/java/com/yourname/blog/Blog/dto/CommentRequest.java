package com.yourname.blog.Blog.dto;

import lombok.Data;

@Data
public class CommentRequest {

    private Long blogId;
    private String content;
}
