package com.yourname.blog.Blog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/** Local-only mail substitute: preserves the OTP workflow without sending real email. */
@Slf4j
@Service
@Profile("local")
public class LocalEmailService implements EmailService {
    @Override
    public void sendOtp(String to, String otp) {
        log.info("Local development OTP for {}: {}", to, otp);
    }
}
