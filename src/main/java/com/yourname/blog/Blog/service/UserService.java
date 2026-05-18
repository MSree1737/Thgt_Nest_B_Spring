package com.yourname.blog.Blog.service;

import com.yourname.blog.Blog.dto.UserRequest;
import com.yourname.blog.Blog.dto.UserResponse;

public interface UserService {

    UserResponse register(UserRequest request);

}