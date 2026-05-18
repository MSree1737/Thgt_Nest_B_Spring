package com.yourname.blog.Blog.controller;

import com.yourname.blog.Blog.dto.LoginRequest;
import com.yourname.blog.Blog.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class JWTAuthController {

    private final AuthService authService;

    public JWTAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        return authService.login(request);
    }
}