package com.yourname.blog.Blog.service;

import com.yourname.blog.Blog.entity.Blog;
import com.yourname.blog.Blog.entity.User;
import com.yourname.blog.Blog.exception.ResourceNotFoundException;
import com.yourname.blog.Blog.repository.BlogRepository;
import com.yourname.blog.Blog.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public BlogServiceImpl(BlogRepository blogRepository,
                           UserRepository userRepository) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
    }

    // ✅ Get logged-in user
    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    // ✅ Create Blog
    @Override
    public Blog createBlog(Blog blog) {

        User user = getCurrentUser();
        blog.setAuthor(user);

        return blogRepository.save(blog);
    }

    // ✅ Get Blog by ID
    @Override
    public Blog getBlogById(Long id) {

        return blogRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Blog not found"));
    }

    // ✅ Update Blog
    @Override
    public Blog updateBlog(Long id, Blog updatedBlog) {

        Blog blog = getBlogById(id);
        User currentUser = getCurrentUser();

        if (!blog.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to update this blog");
        }

        blog.setTitle(updatedBlog.getTitle());
        blog.setContent(updatedBlog.getContent());

        return blogRepository.save(blog);
    }

    // ✅ Delete Blog
    @Override
    public void deleteBlog(Long id) {

        Blog blog = getBlogById(id);
        User currentUser = getCurrentUser();

        if (!blog.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to delete this blog");
        }

        blogRepository.delete(blog);
    }

    // ✅ Get My Blogs
    @Override
    public List<Blog> getMyBlogs() {

        User user = getCurrentUser();
        return blogRepository.findByAuthor(user);
    }

    // ✅ Pagination + Sorting
    @Override
    public Page<Blog> getAllBlogs(int page, int size, String sortBy) {

        PageRequest pageable =
                PageRequest.of(page, size, Sort.by(sortBy).descending());

        return blogRepository.findAll(pageable);
    }

    // ✅ Search Blogs
    @Override
    public List<Blog> searchBlogs(String keyword) {

        return blogRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                        keyword, keyword);
    }
}