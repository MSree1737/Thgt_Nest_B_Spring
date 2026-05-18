package com.yourname.blog.Blog.repository;

import com.yourname.blog.Blog.entity.Blog;
import com.yourname.blog.Blog.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    Page<Blog> findAll(Pageable pageable);

    List<Blog> findByAuthor(User user);

    List<Blog> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title, String content);
}