package com.example.RaySolution.Controller;

import com.example.RaySolution.DTO.MyProducerDTO;
import com.example.RaySolution.DTO.OrderDTO;
import com.example.RaySolution.Service.MyProducerService;
import com.example.RaySolution.Service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/login/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO.OrderResponse> createOrder(
            @Valid @RequestBody OrderDTO.CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO.OrderResponse>> getAllOrder() {
        return ResponseEntity.ok(orderService.getAllOrder());
    }

    @GetMapping("/admin")
    public ResponseEntity<List<OrderDTO.OrderResponse>> getAllOrderAdmin() {
        return ResponseEntity.ok(orderService.getAllOrderAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO.OrderResponse> getOrderByID(
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderByID(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<OrderDTO.OrderResponse> getOrderByCode(
            @PathVariable String code) {
        return ResponseEntity.ok(orderService.getOrderByCode(code));
    }

    @GetMapping("/code/{code}/admin")
    public ResponseEntity<OrderDTO.OrderResponse> getOrderByCodeAdmin(
            @PathVariable String code) {
        return ResponseEntity.ok(orderService.getOrderByCodeAdmin(code));
    }

    @PutMapping("/{code}/producer")
    public ResponseEntity<OrderDTO.OrderResponse> updateOrder(
            @PathVariable String code,
            @Valid @RequestBody OrderDTO.UpdateOrderRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(request, code));
    }

    @PutMapping("/{code}/status")
    public ResponseEntity<OrderDTO.OrderResponse> updateOrderState(
            @PathVariable String code,
            @Valid @RequestBody OrderDTO.UpdateStateRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(request, code));
    }

    @PutMapping("/{code}/status/admin")
    public ResponseEntity<OrderDTO.OrderResponse> updateOrderStateAdmin(
            @PathVariable String code,
            @Valid @RequestBody OrderDTO.UpdateStateRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatusAdmin(request, code));
    }

    @PutMapping("/{code}/confirmrequset")
    public ResponseEntity<OrderDTO.OrderResponse> updateOrderInformation(
            @PathVariable String code,
            @Valid @RequestBody OrderDTO.UpdateInformationRequest request) {
        return ResponseEntity.ok(orderService.updateInformationStatus(request, code));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteOrder(@PathVariable String code) {
        orderService.deleteOrder(code);
        return ResponseEntity.ok("Order deleted");
    }
}
