package com.example.RaySolution.DTO;

import com.example.RaySolution.Brand;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


public class ProducerDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProducerRequest {
        @NotBlank(message = "Code is required")
        public String code;

        @NotBlank(message = "Name is required")
        public String name;

        @NotBlank(message = "description is required")
        public String description;
        //        @NotBlank(message = "Image is required")
        public byte[] image;
        public Integer stowage;

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProducerGroupRequest {
        @NotBlank(message = "Code is required")
        public String code;

        @NotBlank(message = "Name is required")
        public String name;

        @NotBlank(message = "description is required")
        public String description;

        //        @NotBlank(message = "Image is required")
        public byte[] image;

        public Integer stowage;

    }

    @Builder
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProducerResponse {
        public Long id;
        public String code;
        public String name;
        public String description;
        public byte[] image;
        public Integer stowage;
        public Brand brand;
        public Integer producerAdd;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProducerRequest {

        public String code;
        public String name;
        public String description;
        public byte[] image;
        public Integer stowage;
        public Brand brand;

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProducerRequestAdd {
        public Integer producerAdd;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateMessage {
        public Long id;
        public String code;
        public String name;
        public String description;
        public byte[] image;
        public Integer stowage;
        public Brand brand;
        public Integer producerAdd;
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class producerAddUpdateMessage {
        public Integer stowage;
        public Integer producerAdd;
        private String message;
    }


}
