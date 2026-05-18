package com.yourname.blog.Blog.service;

import com.yourname.blog.Blog.entity.Blog;
import com.yourname.blog.Blog.entity.Like;
import com.yourname.blog.Blog.entity.User;
import com.yourname.blog.Blog.repository.BlogRepository;
import com.yourname.blog.Blog.repository.LikeRepository;
import com.yourname.blog.Blog.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    @Override
    public String toggleLike(Long blogId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        return likeRepository.findByBlogAndUser(blog, user)
                .map(existingLike -> {
                    likeRepository.delete(existingLike);
                    return "Blog unliked";
                })
                .orElseGet(() -> {
                    Like like = Like.builder()
                            .blog(blog)
                            .user(user)
                            .build();

                    likeRepository.save(like);
                    return "Blog liked";
                });
    }

    @Override
    public long getLikeCount(Long blogId) {

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        return likeRepository.countByBlog(blog);
    }
}