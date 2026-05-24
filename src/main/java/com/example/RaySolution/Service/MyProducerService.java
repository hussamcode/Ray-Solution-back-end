package com.example.RaySolution.Service;

import com.example.RaySolution.DTO.MyProducerDTO;
import com.example.RaySolution.Repository.MyProducerRepository;
import com.example.RaySolution.Repository.UserRepository;
import com.example.RaySolution.model.MyProducer;
import com.example.RaySolution.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class MyProducerService {

    private final MyProducerRepository producerRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // Helper to get current logged-in user
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public MyProducerDTO.ProducerResponse createProducer(MyProducerDTO.CreateProducerRequest request) {
        User currentUser = getCurrentUser();
        MyProducer myproducer = MyProducer.builder()
                .code(request.code)
                .producerAdd(request.producerAdd)
                .user(currentUser) // ✅ link to user
                .build();

        producerRepository.save(myproducer);
        notifyProducerStatus(myproducer, "ok");
        return mapToResponse(myproducer);
    }

    public List<MyProducerDTO.ProducerResponse> getAllShipments() {
        User currentUser = getCurrentUser();
        return producerRepository.findAllByUser(currentUser) // ✅ only current user's data
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public MyProducerDTO.ProducerResponse getProducerByID(Long id) {
        User currentUser = getCurrentUser();
        MyProducer myproducer = producerRepository.findByIdAndUser(id, currentUser) // ✅
                .orElseThrow(() -> new RuntimeException("Producer not found"));
        return mapToResponse(myproducer);
    }

    public MyProducerDTO.ProducerResponse getProducerByCode(String code) {
        User currentUser = getCurrentUser();
        MyProducer myproducer = producerRepository.findBycodeAndUser(code, currentUser) // ✅
                .orElseThrow(() -> new RuntimeException("Producer not found"));
        return mapToResponse(myproducer);
    }

    public MyProducerDTO.ProducerResponse updateProducerStatus(MyProducerDTO.UpdateProducerRequest request, Long id) {
        User currentUser = getCurrentUser();
        MyProducer myproducer = producerRepository.findByIdAndUser(id, currentUser) // ✅
                .orElseThrow(() -> new RuntimeException("Producer not found"));

        if (request.getProducerAdd() != null) myproducer.setProducerAdd(request.getProducerAdd());
        myproducer = producerRepository.save(myproducer);
        notifyProducerStatus(myproducer, "test");
        return mapToResponse(myproducer);
    }

    public void deleteProducer(Long id) {
        User currentUser = getCurrentUser();
        MyProducer producer = producerRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Producer not found"));

        producerRepository.delete(producer);
        notifyProducerStatus(producer, "deleted"); // إشعار WebSocket بالحذف
    }

    public void notifyProducerStatus(MyProducer producer, String message) {

        // 1. بناء رسالة التحديث
        var update = MyProducerDTO.StatusUpdateMessage.builder()
                .id(producer.getId())
                .code(producer.getCode())
                .producerAdd(producer.getProducerAdd())
                .message(message) // "ok" / "deleted" / "test"
                .build();

        // 2. إرسال لكل المتصلين بهذا الـ topic
        messagingTemplate.convertAndSend("/topic/producer", update);
        //                                      ↑
        //                          كل المتصلين يستقبلون التحديث

        // 3. إرسال لـ topic خاص بهذا المنتج فقط
        messagingTemplate.convertAndSend("/topic/producer" + producer.getId(), update);
        //                                      ↑
        //                          مثلاً: /topic/producer1 أو /topic/producer5
    }

    private MyProducerDTO.ProducerResponse mapToResponse(MyProducer producer) {
        return MyProducerDTO.ProducerResponse.builder()
                .id(producer.getId())
                .code(producer.getCode())
                .producerAdd(producer.getProducerAdd())
                .userId(producer.getUser().getId())
                .build();
    }
}
