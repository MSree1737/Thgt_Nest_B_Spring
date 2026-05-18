package com.yourname.blog.Blog.repository;

import com.yourname.blog.Blog.entity.Like;
import com.yourname.blog.Blog.entity.Blog;
import com.yourname.blog.Blog.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByBlogAndUser(Blog blog, User user);

    long countByBlog(Blog blog);
}