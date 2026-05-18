package com.yourname.blog.Blog.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public String healthCheck() {
        return "Blog Backend Running";
    }
}