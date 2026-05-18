package com.yourname.blog.Blog.service;

import com.yourname.blog.Blog.dto.CommentRequest;
import com.yourname.blog.Blog.entity.Comment;

import java.util.List;

public interface CommentService {

    Comment addComment(CommentRequest request);

    List<Comment> getCommentsByBlog(Long blogId);
}
