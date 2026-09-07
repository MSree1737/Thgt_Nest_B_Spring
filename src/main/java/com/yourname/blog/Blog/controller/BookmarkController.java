package com.yourname.blog.Blog.controller;
import com.yourname.blog.Blog.entity.*; import com.yourname.blog.Blog.repository.*; import com.yourname.blog.Blog.dto.ArticleResponse; import com.yourname.blog.Blog.service.ArticleMapper; import jakarta.transaction.Transactional; import lombok.RequiredArgsConstructor; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/bookmarks") @RequiredArgsConstructor
public class BookmarkController { private final BookmarkRepository bookmarks; private final BlogRepository blogs; private final UserRepository users; private final ArticleMapper mapper;
 private User user(){return users.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new RuntimeException("User not found"));}
 @PostMapping("/{blogId}") public Map<String,Object> toggle(@PathVariable Long blogId){ Blog b=blogs.findById(blogId).orElseThrow(()->new RuntimeException("Blog not found")); User u=user(); var old=bookmarks.findByBlogAndUser(b,u); if(old.isPresent()){bookmarks.delete(old.get());return Map.of("bookmarked",false);} bookmarks.save(Bookmark.builder().blog(b).user(u).build());return Map.of("bookmarked",true); }
 @GetMapping @Transactional public List<ArticleResponse> mine(){return bookmarks.findByUser(user()).stream().map(Bookmark::getBlog).map(mapper::map).toList();}
}
