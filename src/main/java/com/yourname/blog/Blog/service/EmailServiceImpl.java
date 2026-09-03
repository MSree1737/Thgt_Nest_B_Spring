package com.yourname.blog.Blog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final RestClient restClient = RestClient.create();

    @Value("${brevo.api.key:${MAIL_PASSWORD:}}")
    private String apiKey;

    @Value("${spring.mail.username:mannesri2005@gmail.com}")
    private String senderEmail;

    @Value("${brevo.sender.name:ThoughtNest}")
    private String senderName;

    @Override
    public void sendOtp(String to, String otp) {
        log.info("Sending OTP email to {} via Brevo REST API", to);

        Map<String, Object> requestBody = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", to)),
                "subject", "Your OTP Verification Code",
                "htmlContent", "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">" +
                        "<h2>Your OTP Verification Code</h2>" +
                        "<p>Your verification code is: <strong style=\"font-size: 24px; color: #4F46E5;\">" + otp + "</strong></p>" +
                        "<p>This code will expire in 10 minutes.</p>" +
                        "</div>"
        );

        try {
            String response = restClient.post()
                    .uri("https://api.brevo.com/v3/smtp/email")
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            log.info("Brevo email sent successfully to {}. Response: {}", to, response);
        } catch (Exception e) {
            log.error("Failed to send email via Brevo REST API to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage(), e);
        }
    }
}