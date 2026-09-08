package com.yourname.blog.Blog.controller;

import com.yourname.blog.Blog.dto.UserResponse;
import com.yourname.blog.Blog.exception.ResourceNotFoundException;
import com.yourname.blog.Blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getName(), user.getEmail()));
    }
}

