package com.example.RaySolution.Controller;

import com.example.RaySolution.DTO.UserDTO;
import com.example.RaySolution.Repository.UserRepository;
import com.example.RaySolution.Role;
import com.example.RaySolution.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ جيب كل المستخدمين
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // ✅ أنشئ Admin جديد
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody UserDTO.CreateUserRequest request) {
        User admin = User.builder()
                .username(request.username)
                .email(request.email)
                .password(passwordEncoder.encode(request.password))
                .role(Role.ADMIN) // ✅
                .enabled(true)
                .build();
        userRepository.save(admin);
        return ResponseEntity.ok("Admin created successfully");
    }

    // ✅ غيّر role المستخدم
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestParam Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        userRepository.save(user);
        return ResponseEntity.ok("Role updated successfully");
    }
}
