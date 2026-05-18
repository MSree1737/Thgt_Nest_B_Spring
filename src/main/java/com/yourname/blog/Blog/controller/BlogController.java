package com.yourname.blog.Blog.controller;

import com.yourname.blog.Blog.entity.Blog;
import com.yourname.blog.Blog.service.BlogService;
import com.yourname.blog.Blog.util.ApiResponse;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    // ✅ Create Blog
    @PostMapping
    public ResponseEntity<ApiResponse<Blog>> createBlog(@RequestBody Blog blog) {

        Blog savedBlog = blogService.createBlog(blog);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Blog created successfully", savedBlog)
        );
    }

    // ✅ Get All Blogs (Pagination + Sorting)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Blog>>> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Page<Blog> blogs = blogService.getAllBlogs(page, size, sortBy);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Blogs fetched successfully", blogs)
        );
    }

    // ✅ Get Blog By ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Blog>> getBlog(@PathVariable Long id) {

        Blog blog = blogService.getBlogById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Blog fetched successfully", blog)
        );
    }

    // ✅ Update Blog
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Blog>> updateBlog(
            @PathVariable Long id,
            @RequestBody Blog blog) {

        Blog updatedBlog = blogService.updateBlog(id, blog);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Blog updated successfully", updatedBlog)
        );
    }

    // ✅ Delete Blog
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBlog(@PathVariable Long id) {

        blogService.deleteBlog(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Blog deleted successfully", null)
        );
    }

    // ✅ Search Blogs
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Blog>>> searchBlogs(
            @RequestParam String keyword) {

        List<Blog> blogs = blogService.searchBlogs(keyword);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Blogs fetched successfully", blogs)
        );
    }
}