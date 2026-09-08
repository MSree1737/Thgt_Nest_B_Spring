package com.yourname.blog.Blog.service;

import com.yourname.blog.Blog.dto.LoginRequest;
import com.yourname.blog.Blog.entity.User;
import com.yourname.blog.Blog.exception.ResourceNotFoundException;
import com.yourname.blog.Blog.repository.UserRepository;
import com.yourname.blog.Blog.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!user.isVerified()) {
            throw new IllegalStateException("Please verify your email before signing in");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}
