package com.yourname.blog.Blog.service;

import com.yourname.blog.Blog.entity.Blog;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BlogService {

    Blog createBlog(Blog blog);

    Blog getBlogById(Long id);

    Blog updateBlog(Long id, Blog blog);

    void deleteBlog(Long id);

    Page<Blog> getAllBlogs(int page, int size, String sortBy);

    List<Blog> getMyBlogs();

    List<Blog> searchBlogs(String keyword);
}