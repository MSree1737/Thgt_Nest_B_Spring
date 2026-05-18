package com.yourname.blog.Blog.repository;

import com.yourname.blog.Blog.entity.Comment;
import com.yourname.blog.Blog.entity.Blog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBlog(Blog blog);
}
