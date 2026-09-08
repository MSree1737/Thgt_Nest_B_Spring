package com.yourname.blog.Blog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourname.blog.Blog.dto.LoginRequest;
import com.yourname.blog.Blog.dto.OtpVerifyRequest;
import com.yourname.blog.Blog.dto.UserRequest;
import com.yourname.blog.Blog.repository.OtpRepository;
import com.yourname.blog.Blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        otpRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testRegistrationOtpVerificationAndLogin() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("Test User");
        userRequest.setEmail("testuser@example.com");
        userRequest.setPassword("password123");

        // 1. Register
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        // 2. Fetch OTP from repository
        var otpRecord = otpRepository.findTopByEmailOrderByIdDesc("testuser@example.com")
                .orElseThrow();
        String generatedOtp = otpRecord.getOtp();

        // 3. Verify OTP
        OtpVerifyRequest verifyRequest = new OtpVerifyRequest();
        verifyRequest.setEmail("testuser@example.com");
        verifyRequest.setOtp(generatedOtp);

        mockMvc.perform(post("/api/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 4. Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testuser@example.com");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void publicArticleFeedIsAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk());
    }
}
