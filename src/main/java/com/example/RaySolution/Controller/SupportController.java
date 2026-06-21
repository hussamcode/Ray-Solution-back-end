package com.example.RaySolution.Controller;

import com.example.RaySolution.DTO.SupportRequest;
import com.example.RaySolution.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final EmailService emailService;

    public SupportController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> sendSupportMessage(@Valid @RequestBody SupportRequest request) {
        try {
            emailService.sendSupportEmail(
                    request.getName(),
                    request.getEmail(),
                    request.getCategory(),
                    request.getMessage()
            );
            return ResponseEntity.ok(Map.of("message", "Support ticket sent successfully"));
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Failed to send support ticket"));
        }
    }
}
