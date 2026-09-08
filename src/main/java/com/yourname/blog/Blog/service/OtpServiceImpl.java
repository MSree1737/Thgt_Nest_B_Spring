package com.yourname.blog.Blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.yourname.blog.Blog.exception.ResourceNotFoundException;
import com.yourname.blog.Blog.repository.OtpRepository;
import com.yourname.blog.Blog.entity.OtpVerification;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public void generateAndSendOtp(String email) {

        String otp = String.valueOf(SECURE_RANDOM.nextInt(900000) + 100000);

        OtpVerification otpEntity = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        otpRepository.save(otpEntity);

        emailService.sendOtp(email, otp);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {

        var record = otpRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new ResourceNotFoundException("OTP not found for email: " + email));

        if (record.isUsed())
            throw new IllegalStateException("OTP already used");

        if (record.getExpiryTime().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("OTP expired");

        if (!record.getOtp().equals(otp))
            throw new IllegalArgumentException("Invalid OTP");

        record.setUsed(true);
        otpRepository.save(record);

        return true;
    }
}