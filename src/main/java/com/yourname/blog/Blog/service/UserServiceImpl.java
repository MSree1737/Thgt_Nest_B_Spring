package com.yourname.blog.Blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.yourname.blog.Blog.repository.UserRepository;
import com.yourname.blog.Blog.entity.User;
import com.yourname.blog.Blog.dto.*;
import com.yourname.blog.Blog.exception.ResourceNotFoundException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserResponse register(UserRequest request) {

        var existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.isVerified()) {
                throw new RuntimeException("Email already registered");
            }

            // The account was created before OTP verification.  Let the controller
            // send a new OTP instead of preventing the user from completing signup.
            return new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
