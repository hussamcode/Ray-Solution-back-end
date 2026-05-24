package com.example.RaySolution.Service;

import com.example.RaySolution.DTO.MyProducerDTO;
import com.example.RaySolution.DTO.OrderDTO;
import com.example.RaySolution.DTO.ProducerDTO;
import com.example.RaySolution.OrderStatus;
import com.example.RaySolution.Repository.OrderRepository;
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

import java.io.Console;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;


    private boolean isAdmin() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isManger() {
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

    public OrderDTO.OrderResponse createOrder(OrderDTO.CreateOrderRequest request) {
        User currentUser = getCurrentUser();
        Order order = Order.builder()
                .producerCode(new String[]{request.producerCode})
                .user(currentUser)
                .build();

        orderRepository.save(order);
        notifyOrderStatus(order, "ok");
        return mapToResponse(order);
    }


    public OrderDTO.OrderResponse updateOrder(OrderDTO.UpdateOrderRequest request, String code) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findBycodeAndUser(code, currentUser)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (request.producerCode != null) {
            String[] existing = order.getProducerCode();
            String[] updated = new String[existing.length + 1];
            System.arraycopy(existing, 0, updated, 0, existing.length);
            updated[existing.length] = request.producerCode;
            order.setProducerCode(updated);
        }

        order = orderRepository.save(order);
        notifyOrderStatus(order, "ok");
        return mapToResponse(order);
    }

    public OrderDTO.OrderResponse updateOrderStatus(OrderDTO.UpdateStateRequest request, String code) {
        Order order;
        User currentUser = getCurrentUser();
        order = orderRepository.findBycodeAndUser(code, currentUser)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(request.getStatus());
        if (request.status == OrderStatus.PENDING_APPROVAL) {
            order.setAcceptableAT(LocalDateTime.now());
        }
        order = orderRepository.save(order);
        notifyOrderStatus(order, "ok");
        return mapToResponse(order);
    }

    public OrderDTO.OrderResponse updateOrderStatusAdmin(OrderDTO.UpdateStateRequest request, String code) {
        Order order;
        if (isAdmin() || isManger()) {
            order = orderRepository.findBycode(code)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
        } else {
            User currentUser = getCurrentUser();
            order = orderRepository.findBycodeAndUser(code, currentUser)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
        }

        order.setStatus(request.getStatus());
        if (request.getStatus() == OrderStatus.PENDING_APPROVAL) {
            order.setAcceptableAT(LocalDateTime.now());
        }
        System.out.println(request.getStatus() == OrderStatus.PROCESSED);

        if (request.getStatus() == OrderStatus.PROCESSED) {
            System.out.println(request.getDeliveryAt());
            order.setDeliveryAt(request.getDeliveryAt());
        }
        order = orderRepository.save(order);
        notifyOrderStatus(order, "ok");
        return mapToResponse(order);
    }


    public OrderDTO.OrderResponse updateInformationStatus(OrderDTO.UpdateInformationRequest request, String code) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findBycodeAndUser(code, currentUser)
                .orElseThrow(() -> new RuntimeException("order not found with id: " + code));

        order.setName(request.getName());
        order.setPhonenumber(request.getPhonenumber());
        order.setEstablishmentname(request.getEstablishmentname());
        order.setStatus(request.getStatus());
        if (request.status == OrderStatus.PENDING_APPROVAL) {
            order.setAcceptableAT(LocalDateTime.now());
        }
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
        if (isAdmin() || isManger()) {
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
        if (isAdmin() || isManger()) {
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


    public void deleteOrder(String code) {
        Order order;
        if (isAdmin() || isManger()) {
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
                .build();
    }
}
