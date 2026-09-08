package com.yourname.blog.Blog.controller;

import com.yourname.blog.Blog.dto.LoginRequest;
import com.yourname.blog.Blog.service.AuthService;
import com.yourname.blog.Blog.util.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class JWTAuthController {

    private final AuthService authService;

    public JWTAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Login successful", Map.of("token", token))
        );
    }
}
