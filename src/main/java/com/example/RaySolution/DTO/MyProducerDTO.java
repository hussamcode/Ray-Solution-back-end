package com.example.RaySolution.DTO;

import com.example.RaySolution.Brand;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class MyProducerDTO {


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProducerResponse {
        public Long id;
        public String code;
        public Integer producerAdd;
        public Long userId;

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProducerRequest {
        @NotBlank(message = "Code is required")
        public String code;
        public Integer producerAdd;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProducerRequest {

        public Integer producerAdd;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateMessage {
        public Long id;
        public String code;
        public Integer producerAdd;
        private String message; // "ok" / "deleted" / "test"
    }
}
