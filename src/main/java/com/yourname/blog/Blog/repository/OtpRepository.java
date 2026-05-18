package com.yourname.blog.Blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yourname.blog.Blog.entity.OtpVerification;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByEmailOrderByIdDesc(String email);
}