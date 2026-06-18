package com.example.RaySolution.Service;

import com.example.RaySolution.DTO.MyProducerDTO;
import com.example.RaySolution.DTO.OrderDTO;
import com.example.RaySolution.DTO.ProducerDTO;
import com.example.RaySolution.OrderStatus;
import com.example.RaySolution.Repository.OrderRepository;
import com.example.RaySolution.Repository.ProducerRepository;
import com.example.RaySolution.Repository.UserRepository;
import com.example.RaySolution.model.MyProducer;
import com.example.RaySolution.model.Order;
import com.example.RaySolution.model.Producer;
import com.example.RaySolution.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProducerRepository producerRepository;
    private final SimpMessagingTemplate messagingTemplate;


    private boolean isAdmin() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isManager() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Producer findActiveProduct(String code) {
        Producer producer = producerRepository.findBycode(code)
                .orElseThrow(() -> new RuntimeException("Product not found with code: " + code));
        if (!producer.isActive()) {
            throw new RuntimeException("Product is no longer available: " + code);
        }
        return producer;
    }

    @Transactional
    public OrderDTO.OrderResponse createOrder(OrderDTO.CreateOrderRequest request) {
        User currentUser = getCurrentUser();
        findActiveProduct(request.producerCode);

        Order order = Order.builder()
                .producerCode(new ArrayList<>(List.of(request.producerCode)))
                .user(currentUser)
                .build();

        orderRepository.save(order);
        notifyOrderStatus(order, "ok");
        return mapToResponse(order);
    }


    @Transactional
    public OrderDTO.OrderResponse updateOrder(OrderDTO.UpdateOrderRequest request, String code) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findBycodeAndUser(code, currentUser)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (request.producerCode != null) {
            findActiveProduct(request.producerCode);
            List<String> existing = order.getProducerCode();
            existing.add(request.producerCode);
            order.setProducerCode(existing);
        }

        order = orderRepository.save(order);
        notifyOrderStatus(order, "ok");
        return mapToResponse(order);
    }

    @Transactional
    public OrderDTO.OrderResponse updateOrderStatus(OrderDTO.UpdateStateRequest request, String code) {
        Order order;
        User currentUser = getCurrentUser();
        order = orderRepository.findBycodeAndUser(code, currentUser)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean wasProcessing = order.getStatus() == OrderStatus.PROCESSING;
        order.setStatus(request.getStatus());
        if (request.status == OrderStatus.PENDING_APPROVAL) {
            order.setAcceptableAT(LocalDateTime.now());
        }
        if (!wasProcessing && request.getStatus() == OrderStatus.PROCESSING) {
            deductStock(order);
        }
        order = orderRepository.save(order);
        notifyOrderStatus(order, "ok");
        return mapToResponse(order);
    }

    @Transactional
    public OrderDTO.OrderResponse updateOrderStatusAdmin(OrderDTO.UpdateStateRequest request, String code) {
        Order order;
        if (isAdmin() || isManager()) {
            order = orderRepository.findBycode(code)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
        } else {
            User currentUser = getCurrentUser();
            order = orderRepository.findBycodeAndUser(code, currentUser)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
        }

        boolean wasProcessing = order.getStatus() == OrderStatus.PROCESSING;
        order.setStatus(request.getStatus());
        if (request.getStatus() == OrderStatus.PENDING_APPROVAL) {
            order.setAcceptableAT(LocalDateTime.now());
        }
        if (!wasProcessing && request.getStatus() == OrderStatus.PROCESSING) {
            deductStock(order);
        }
        if (request.getStatus() == OrderStatus.PROCESSED) {
            order.setDeliveryAt(request.getDeliveryAt());
        }
        if (request.getStatus() == OrderStatus.DELIVERED) {
            order.setDeliveryAt(LocalDateTime.now());
        }
        order = orderRepository.save(order);
        notifyOrderStatus(order, "ok");
        return mapToResponse(order);
    }


    @Transactional
    public OrderDTO.OrderResponse updateInformationStatus(OrderDTO.UpdateInformationRequest request, String code) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findBycodeAndUser(code, currentUser)
                .orElseThrow(() -> new RuntimeException("order not found with id: " + code));

        boolean wasProcessing = order.getStatus() == OrderStatus.PROCESSING;
        order.setName(request.getName());
        order.setPhonenumber(request.getPhonenumber());
        order.setEstablishmentname(request.getEstablishmentname());
        order.setStatus(request.getStatus());
        if (request.getLatitude() != null) {
            order.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            order.setLongitude(request.getLongitude());
        }
        if (request.getAddress() != null) {
            order.setAddress(request.getAddress());
        }
        if (request.status == OrderStatus.PENDING_APPROVAL) {
            order.setAcceptableAT(LocalDateTime.now());
        }
        if (!wasProcessing && request.getStatus() == OrderStatus.PROCESSING) {
            deductStock(order);
        }
        order = orderRepository.save(order);

        notifyOrderStatus(order, "ok");

        return mapToResponse(order);
    }


    @Transactional
    public OrderDTO.OrderResponse updateOrderLocation(OrderDTO.UpdateLocationRequest request, String code) {
        Order order;
        if (isAdmin() || isManager()) {
            order = orderRepository.findBycode(code)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
        } else {
            User currentUser = getCurrentUser();
            order = orderRepository.findBycodeAndUser(code, currentUser)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if (order.getStatus() != OrderStatus.PENDING_APPROVAL
                    && order.getStatus() != OrderStatus.PROCESSING) {
                throw new RuntimeException("Location can only be changed during Pending Approval or Processing");
            }
        }

        order.setLatitude(request.getLatitude());
        order.setLongitude(request.getLongitude());
        order.setAddress(request.getAddress());
        order = orderRepository.save(order);

        notifyOrderStatus(order, "ok");
        return mapToResponse(order);
    }


    public List<OrderDTO.OrderResponse> getAllOrder() {
        User currentUser = getCurrentUser();
        return orderRepository.findAllByUser(currentUser)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderDTO.OrderResponse> getAllOrderAdmin() {
        if (isAdmin() || isManager()) {
            return orderRepository.findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        User currentUser = getCurrentUser();
        return orderRepository.findAllByUser(currentUser)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderDTO.OrderResponse getOrderByID(Long id) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findByIdAndUser(id, currentUser) // ✅
                .orElseThrow(() -> new RuntimeException("Producer not found"));
        return mapToResponse(order);
    }

    public OrderDTO.OrderResponse getOrderByCode(String code) {

        User currentUser = getCurrentUser();
        return mapToResponse(
                orderRepository.findBycodeAndUser(code, currentUser)
                        .orElseThrow(() -> new RuntimeException("Order not found"))
        );
    }

    public OrderDTO.OrderResponse getOrderByCodeAdmin(String code) {
        if (isAdmin() || isManager()) {
            return mapToResponse(
                    orderRepository.findBycode(code)
                            .orElseThrow(() -> new RuntimeException("Order not found"))
            );
        }
        User currentUser = getCurrentUser();
        return mapToResponse(
                orderRepository.findBycodeAndUser(code, currentUser)
                        .orElseThrow(() -> new RuntimeException("Order not found"))
        );
    }


    @Transactional
    public void deleteOrder(String code) {
        Order order;
        if (isAdmin() || isManager()) {
            order = orderRepository.findBycode(code)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
        } else {
            User currentUser = getCurrentUser();
            order = orderRepository.findBycodeAndUser(code, currentUser)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
        }
        orderRepository.delete(order);
        notifyOrderStatus(order, "deleted");
    }


    @Transactional
    private void deductStock(Order order) {
        for (String code : order.getProducerCode()) {
            Producer product = producerRepository.findBycode(code)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + code));
            if (product.getStowage() < 1) {
                throw new RuntimeException("Insufficient stock for product: " + code);
            }
            product.setStowage(product.getStowage() - 1);
            producerRepository.save(product);
        }
    }

    public void notifyOrderStatus(Order order, String message) {

        var update = OrderDTO.StatusUpdateMessage.builder()
                .id(order.getId())
                .code(order.getCode())
                .producerCode(order.getProducerCode())
                .name(order.getName())
                .acceptableAT(order.getAcceptableAT())
                .establishmentname(order.getEstablishmentname())
                .deliveryAt(order.getDeliveryAt())
                .phonenumber(order.getPhonenumber())
                .latitude(order.getLatitude())
                .longitude(order.getLongitude())
                .address(order.getAddress())
                .message(message)
                .build();

        messagingTemplate.convertAndSend("/topic/order", update);


        messagingTemplate.convertAndSend("/topic/order/" + order.getId(), update);
    }


    private OrderDTO.OrderResponse mapToResponse(Order order) {
        return OrderDTO.OrderResponse.builder()
                .id(order.getId())
                .code(order.getCode())
                .producerCode(order.getProducerCode())
                .userId(order.getUser().getId())
                .acceptableAT(order.getAcceptableAT())
                .status(order.getStatus())
                .deliveryAt(order.getDeliveryAt())
                .phonenumber(order.getPhonenumber())
                .establishmentname(order.getEstablishmentname())
                .name(order.getName())
                .latitude(order.getLatitude())
                .longitude(order.getLongitude())
                .address(order.getAddress())
                .build();
    }
}
