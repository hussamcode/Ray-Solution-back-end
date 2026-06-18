package com.example.RaySolution.DTO;

import com.example.RaySolution.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class UserDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {
        @NotBlank(message = "username is required")
        public String username;

        @NotBlank(message = "email is required")
        public String email;

        @NotBlank(message = "password is required")
        public String password;

        public String role;

    }

    @Builder
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        public Long id;
        public String username;
        public String email;
        public String role;

        public static UserResponse fromUser(User user) {
            return UserResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .build();
        }
    }

}
