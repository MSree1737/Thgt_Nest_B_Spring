package com.yourname.blog.Blog.controller;

import com.yourname.blog.Blog.dto.FollowResponse;
import com.yourname.blog.Blog.entity.Follow;
import com.yourname.blog.Blog.entity.User;
import com.yourname.blog.Blog.exception.ResourceNotFoundException;
import com.yourname.blog.Blog.repository.FollowRepository;
import com.yourname.blog.Blog.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    private User me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @PostMapping("/{targetUserId}")
    @Transactional
    public ResponseEntity<FollowResponse> toggleFollow(@PathVariable Long targetUserId) {
        User current = me();

        if (current.getId().equals(targetUserId)) {
            return ResponseEntity.badRequest().body(
                    new FollowResponse(false, followRepository.countByFollowed(current), followRepository.countByFollower(current), "You cannot follow yourself")
            );
        }

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found with id: " + targetUserId));

        Optional<Follow> existing = followRepository.findByFollowerAndFollowed(current, target);
        boolean nowFollowing;
        String message;

        if (existing.isPresent()) {
            followRepository.delete(existing.get());
            followRepository.flush();
            nowFollowing = false;
            message = "Unfollowed " + target.getName();
        } else {
            followRepository.save(Follow.builder().follower(current).followed(target).build());
            followRepository.flush();
            nowFollowing = true;
            message = "Following " + target.getName();
        }

        long followersCount = followRepository.countByFollowed(target);
        long followingCount = followRepository.countByFollower(target);

        return ResponseEntity.ok(new FollowResponse(nowFollowing, followersCount, followingCount, message));
    }

    @GetMapping("/status/{targetUserId}")
    @Transactional(readOnly = true)
    public ResponseEntity<FollowResponse> getStatus(@PathVariable Long targetUserId) {
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found with id: " + targetUserId));

        boolean following = false;
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> current = userRepository.findByEmail(auth.getName());
            if (current.isPresent()) {
                following = followRepository.existsByFollowerAndFollowed(current.get(), target);
            }
        }

        long followersCount = followRepository.countByFollowed(target);
        long followingCount = followRepository.countByFollower(target);

        return ResponseEntity.ok(new FollowResponse(following, followersCount, followingCount, "Status fetched"));
    }
}
