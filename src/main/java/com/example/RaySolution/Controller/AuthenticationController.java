package com.example.RaySolution.Controller;

import com.example.RaySolution.DTO.LoginUserDto;
import com.example.RaySolution.DTO.ProducerDTO;
import com.example.RaySolution.DTO.RegisterUserDto;
import com.example.RaySolution.DTO.UserDTO;
import com.example.RaySolution.DTO.VerifyUserDto;
import com.example.RaySolution.Service.AuthenticationService;
import com.example.RaySolution.Service.JwtService;

import com.example.RaySolution.Service.ProducerService;
import com.example.RaySolution.model.User;
import com.example.RaySolution.responses.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final ProducerService producerService;
    private final AuthenticationService authenticationService;
    public AuthenticationController(JwtService jwtService, ProducerService producerService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.producerService = producerService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO.UserResponse> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(UserDTO.UserResponse.fromUser(registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            String jwtToken = jwtService.generateToken(
                    authenticatedUser.getUsername(),
                    authenticatedUser.getRole().name()
            );
            return ResponseEntity.ok(new LoginResponse(jwtToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok(Map.of("message", "Verification code sent"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/login/{username}/producer")
    public ResponseEntity<?> getAllProducer(@PathVariable String username) {
        var producer = producerService.getAllShipments();
        return ResponseEntity.status(HttpStatus.OK).body(producer);
    }
}