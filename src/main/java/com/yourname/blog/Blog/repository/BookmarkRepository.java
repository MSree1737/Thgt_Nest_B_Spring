package com.yourname.blog.Blog.repository;
import com.yourname.blog.Blog.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface BookmarkRepository extends JpaRepository<Bookmark,Long>{ Optional<Bookmark> findByBlogAndUser(Blog blog,User user); List<Bookmark> findByUser(User user); }
