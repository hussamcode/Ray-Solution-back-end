package com.example.RaySolution.Controller;

import com.example.RaySolution.DTO.MyProducerDTO;
import com.example.RaySolution.Service.MyProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/login/producer")
@RequiredArgsConstructor
public class MyProducerController {
    private final MyProducerService producerService;

    // إنشاء منتج جديد
    @PostMapping
    public ResponseEntity<MyProducerDTO.ProducerResponse> createProducer(
            @Valid @RequestBody MyProducerDTO.CreateProducerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(producerService.createProducer(request));
    }

    // جيب كل المنتجين
    @GetMapping
    public ResponseEntity<List<MyProducerDTO.ProducerResponse>> getAllShipments() {
        return ResponseEntity.ok(producerService.getAllShipments());
    }

    // جيب منتج بالـ ID
    @GetMapping("/{id}")
    public ResponseEntity<MyProducerDTO.ProducerResponse> getProducerByID(
            @PathVariable Long id) {
        return ResponseEntity.ok(producerService.getProducerByID(id));
    }

    // جيب منتج بالـ Code
    @GetMapping("/code/{code}")
    public ResponseEntity<MyProducerDTO.ProducerResponse> getProducerByCode(
            @PathVariable String code) {
        return ResponseEntity.ok(producerService.getProducerByCode(code));
    }

    // تعديل منتج
    @PutMapping("/{id}")
    public ResponseEntity<MyProducerDTO.ProducerResponse> updateProducerStatus(
            @PathVariable Long id,
            @Valid @RequestBody MyProducerDTO.UpdateProducerRequest request) {
        return ResponseEntity.ok(producerService.updateProducerStatus(request, id));
    }

    // حذف منتج
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProducer(@PathVariable Long id) {
        producerService.deleteProducer(id);
        return ResponseEntity.ok("Producer deleted successfully");
    }
}
