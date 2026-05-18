package com.yourname.blog.Blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.yourname.blog.Blog.repository.OtpRepository;
import com.yourname.blog.Blog.entity.OtpVerification;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    @Override
    public void generateAndSendOtp(String email) {

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        OtpVerification otpEntity = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        otpRepository.save(otpEntity);

        emailService.sendOtp(email, otp);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {

        var record = otpRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (record.isUsed())
            throw new RuntimeException("OTP already used");

        if (record.getExpiryTime().isBefore(LocalDateTime.now()))
            throw new RuntimeException("OTP expired");

        if (!record.getOtp().equals(otp))
            throw new RuntimeException("Invalid OTP");

        record.setUsed(true);
        otpRepository.save(record);

        return true;
    }
}