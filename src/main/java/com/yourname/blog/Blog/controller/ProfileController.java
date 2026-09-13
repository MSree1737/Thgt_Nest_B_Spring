package com.yourname.blog.Blog.controller;

import com.yourname.blog.Blog.dto.UserResponse;
import com.yourname.blog.Blog.entity.User;
import com.yourname.blog.Blog.exception.ResourceNotFoundException;
import com.yourname.blog.Blog.repository.FollowRepository;
import com.yourname.blog.Blog.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<UserResponse> me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        long followersCount = followRepository.countByFollowed(user);
        long followingCount = followRepository.countByFollower(user);

        return ResponseEntity.ok(UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .followedByCurrentUser(false)
                .build());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        long followersCount = followRepository.countByFollowed(user);
        long followingCount = followRepository.countByFollower(user);
        boolean followed = false;

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> current = userRepository.findByEmail(auth.getName());
            if (current.isPresent()) {
                followed = followRepository.existsByFollowerAndFollowed(current.get(), user);
            }
        }

        return ResponseEntity.ok(UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .followedByCurrentUser(followed)
                .build());
    }
}
