package com.yourname.blog.Blog.controller;

import com.yourname.blog.Blog.dto.OtpVerifyRequest;
import com.yourname.blog.Blog.dto.UserRequest;
import com.yourname.blog.Blog.dto.UserResponse;
import com.yourname.blog.Blog.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.yourname.blog.Blog.service.*;
import com.yourname.blog.Blog.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest request) {

        UserResponse response = userService.register(request);
        otpService.generateAndSendOtp(request.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered & OTP SENT successfully", response));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest request) {

        boolean verified = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if (!verified) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Invalid or Expired OTP", null));
        }

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        user.setVerified(true);
        userRepository.save(user);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Account verified successfully", null)
        );
    }


}