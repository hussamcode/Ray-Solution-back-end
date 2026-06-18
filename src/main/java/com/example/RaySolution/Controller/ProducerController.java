package com.example.RaySolution.Controller;

import com.example.RaySolution.Service.ProducerService;
import com.example.RaySolution.model.Producer;
import com.example.RaySolution.DTO.ProducerDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/producer")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProducerController {
    private final ProducerService producerService;

    @PostMapping
    public ResponseEntity<ProducerDTO.ProducerResponse> createProducer(@Valid @RequestBody ProducerDTO.CreateProducerRequest request) {
        var producer = producerService.createProducer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(producer);
    }


    @PostMapping("group")
    public void createProducerGroup(@RequestBody List<Producer> producers) {

        producerService.createProducerGroup(producers);

    }

    @GetMapping
    public ResponseEntity<?> getAllProducer() {
        var producer = producerService.getAllShipments();
        return ResponseEntity.status(HttpStatus.OK).body(producer);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProducersIncludeInactive() {
        var producer = producerService.getAllShipmentsIncludeInactive();
        return ResponseEntity.status(HttpStatus.OK).body(producer);
    }

    @GetMapping("/locations")
    public ResponseEntity<?> getAllLocations() {
        var locations = producerService.getAllLocations();
        return ResponseEntity.status(HttpStatus.OK).body(locations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProducerByID(@PathVariable Long id) {
        ProducerDTO.ProducerResponse producer = producerService.getProducerByID(id);
        return ResponseEntity.status(HttpStatus.OK).body(producer);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getProducerByCode(@PathVariable String code) {
        ProducerDTO.ProducerResponse producer = producerService.getProducerByCode(code);
        return ResponseEntity.status(HttpStatus.OK).body(producer);
    }

    @PutMapping("/{id}/producer")
    public ResponseEntity<?> updateProducer(@PathVariable Long id, @Valid @RequestBody ProducerDTO.UpdateProducerRequest request) {
        ProducerDTO.ProducerResponse producer = producerService.updateProducerStatus(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(producer);
    }

    @PutMapping("/{id}/producerAdd")
    public ResponseEntity<?> updateProducerAdd(@PathVariable Long id, @Valid @RequestBody ProducerDTO.UpdateProducerRequestAdd request) {
        ProducerDTO.ProducerResponse producer = producerService.updateProducerAdd(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(producer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducer(@PathVariable Long id) {
        producerService.deleteProducer(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/csrf-token")
//    public CsrfToken getCsrfToken(HttpServletRequest request) {
//        return (CsrfToken) request.getAttribute("_csrf");
//
//
//    }

}
