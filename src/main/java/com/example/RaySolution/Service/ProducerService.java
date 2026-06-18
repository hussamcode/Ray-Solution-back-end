package com.example.RaySolution.Service;

import com.example.RaySolution.model.Producer;
import com.example.RaySolution.DTO.ProducerDTO;
import com.example.RaySolution.Repository.ProducerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ProducerService {

    private final ProducerRepository producerRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ProducerDTO.ProducerResponse createProducer(ProducerDTO.CreateProducerRequest request) {
        Producer producer = Producer.builder()
                .code(request.code)
                .name(request.name)
                .description(request.description)
                .image(request.image)
                .stowage(request.stowage)
                .brand(request.brand)
                .active(request.active != null ? request.active : true)
                .latitude(request.latitude)
                .longitude(request.longitude)
                .address(request.address)
                .build();

        producerRepository.save(producer);
        notifyProducerStatus(producer, "ok");
        return mapToResponse(producer);
    }

    public void createProducerGroup(List<Producer> requestList) {
        requestList.forEach(request -> {
            Producer producer = Producer.builder()
                    .code(request.getCode())
                    .name(request.getName())
                    .description(request.getDescription())
                    .image(request.getImage())
                    .stowage(request.getStowage())
                    .build();

            producerRepository.save(producer);

        });

    }

    public List<ProducerDTO.ProducerResponse> getAllShipments() {
        List<Producer> producers = producerRepository.findByActiveTrue();
        return producers.stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProducerDTO.ProducerResponse> getAllShipmentsIncludeInactive() {
        List<Producer> producers = producerRepository.findAll();
        return producers.stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProducerDTO.ProducerResponse mapToResponse(Producer producer) {

        return ProducerDTO.ProducerResponse.builder()
                .id(producer.getId())
                .code(producer.getCode())
                .name(producer.getName())
                .description(producer.getDescription())
                .image(producer.getImage())
                .stowage(producer.getStowage())
                .brand(producer.getBrand())
                .producerAdd(producer.getProducerAdd())
                .active(producer.isActive())
                .latitude(producer.getLatitude())
                .longitude(producer.getLongitude())
                .address(producer.getAddress())
                .build();
    }

    public ProducerDTO.ProducerResponse getProducerByID(Long id) {
        Producer producer = producerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + id));
        return mapToResponse(producer);

    }

    public ProducerDTO.ProducerResponse getProducerByCode(String code) {
        Producer producer = producerRepository.findBycode(code)
                .orElseThrow(() -> new RuntimeException("producer not found with code number: " + code));

        return mapToResponse(producer);
    }

    public ProducerDTO.ProducerResponse updateProducerStatus(ProducerDTO.UpdateProducerRequest request, Long id) {
        Producer producer = producerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("producer not found with id:" + id));
        if (request.getCode() != null) {
            producer.setCode(request.getCode());
        }
        if (request.getName() != null) {
            producer.setName(request.getName());
        }
        if (request.getDescription() != null) {
            producer.setDescription(request.getDescription());
        }
        if (request.getImage() != null) {
            producer.setImage(request.getImage());
        }
        if (request.getStowage() != null) {
            producer.setStowage(request.getStowage());
        }
        if (request.getBrand() != null) {
            producer.setBrand(request.getBrand());
        }
        if (request.getActive() != null) {
            producer.setActive(request.getActive());
        }
        if (request.getLatitude() != null) {
            producer.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            producer.setLongitude(request.getLongitude());
        }
        if (request.getAddress() != null) {
            producer.setAddress(request.getAddress());
        }
        producer = producerRepository.save(producer);
        notifyProducerStatus(producer, "test");

        return mapToResponse(producer);
    }

    public ProducerDTO.ProducerResponse updateProducerAdd(ProducerDTO.UpdateProducerRequestAdd request, Long id) {
        Producer producer = producerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("producer not found with id:" + id));

        if (request.getProducerAdd() != null) {
            producer.setProducerAdd(request.getProducerAdd());
        }
        producer = producerRepository.save(producer);
        notifyProducerStatus(producer, "ok");
        return mapToResponse(producer);
    }

    public void deleteProducer(Long id) {
        Producer producer = producerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("producer not found with id:" + id));
        producer.setActive(false);
        producerRepository.save(producer);
        notifyProducerStatus(producer, "deleted");
    }

    public List<ProducerDTO.ProducerLocationResponse> getAllLocations() {
        List<Producer> producers = producerRepository.findByActiveTrue();
        return producers.stream()
                .filter(p -> p.getLatitude() != null && p.getLongitude() != null)
                .map(this::mapToLocationResponse)
                .collect(Collectors.toList());
    }

    private ProducerDTO.ProducerLocationResponse mapToLocationResponse(Producer producer) {
        return ProducerDTO.ProducerLocationResponse.builder()
                .id(producer.getId())
                .code(producer.getCode())
                .name(producer.getName())
                .address(producer.getAddress())
                .latitude(producer.getLatitude())
                .longitude(producer.getLongitude())
                .build();
    }

    public void notifyProducerStatus(Producer producer, String message) {
        var update = ProducerDTO.StatusUpdateMessage.builder()
                .id(producer.getId())
                .code(producer.getCode())
                .name(producer.getName())
                .description(producer.getDescription())
                .image(producer.getImage())
                .brand(producer.getBrand())
                .producerAdd(producer.getProducerAdd())
                .active(producer.isActive())
                .message(message)
                .build();

        messagingTemplate.convertAndSend("/topic/producer", update);
        messagingTemplate.convertAndSend("/topic/producer" + producer.getId(), update);

        log.info("Sent shipment status update: {}", update);
    }
}
