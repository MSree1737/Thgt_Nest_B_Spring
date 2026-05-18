package com.yourname.blog.Blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yourname.blog.Blog.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}