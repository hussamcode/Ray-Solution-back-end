package com.example.RaySolution.Controller;

import com.example.RaySolution.DTO.LoginUserDto;
import com.example.RaySolution.DTO.ProducerDTO;
import com.example.RaySolution.DTO.RegisterUserDto;
import com.example.RaySolution.DTO.VerifyUserDto;
import com.example.RaySolution.Service.AuthenticationService;
import com.example.RaySolution.Service.JwtService;

import com.example.RaySolution.Service.ProducerService;
import com.example.RaySolution.model.User;
import com.example.RaySolution.responses.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final ProducerService producerService;
    private final AuthenticationService authenticationService;
    private String jwtToken = "";

    public AuthenticationController(JwtService jwtService, ProducerService producerService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.producerService = producerService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        if (authenticatedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        // ✅ مرّر الـ role
        jwtToken = jwtService.generateToken(
                authenticatedUser.getUsername(),
                authenticatedUser.getRole().name()
        );
        return ResponseEntity.ok(new LoginResponse(jwtToken));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/login/{username}/producer")
    public ResponseEntity<?> getAllProducer(@PathVariable String username) {
        var producer = producerService.getAllShipments();
        return ResponseEntity.status(HttpStatus.OK).body(producer);
    }
}