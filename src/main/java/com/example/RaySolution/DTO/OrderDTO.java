package com.example.RaySolution.DTO;

import com.example.RaySolution.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

public class OrderDTO {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderResponse {
        private Long id;
        private Long userId;
        private String code;
        private String[] producerCode;
        private LocalDateTime acceptableAT;
        private LocalDateTime deliveryAt;
        private OrderStatus status;
        public String phonenumber;
        public String name;
        public String establishmentname;

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrderRequest {
        public String producerCode;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateOrderRequest {
        public String producerCode;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStateRequest {
        public OrderStatus status;
        private LocalDateTime deliveryAt;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateAtRequest {
        private LocalDateTime acceptableAT;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateMessage {
        public Long id;
        public String code;
        public String[] producerCode;
        public LocalDateTime acceptableAT;
        public LocalDateTime deliveryAt; // ✅ أضف هذا
        public OrderStatus status;
        public String name;
        public String phonenumber;
        public String establishmentname;
        public String message; // "ok" / "deleted" / "test"
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateInformationRequest {
        public String phonenumber;
        public String name;
        public String establishmentname;
        public OrderStatus status;
    }
}
